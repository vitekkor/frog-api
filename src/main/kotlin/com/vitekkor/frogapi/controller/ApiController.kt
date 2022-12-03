package com.vitekkor.frogapi.controller

import com.vitekkor.frogapi.db.repository.UserRepository
import com.vitekkor.frogapi.service.TokenService
import com.vitekkor.frogapi.util.decodeBase64
import com.vitekkor.frogapi.util.encodeBase64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KotlinLogging.logger
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.IMAGE_JPEG_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.io.File

@RestController
@RequestMapping("/api/v1")
class ApiController(private val userRepository: UserRepository, private val tokenService: TokenService) {
    private val logger = logger {}

    @GetMapping("/{token}/frog", produces = [IMAGE_JPEG_VALUE])
    suspend fun getFrog(@PathVariable token: String): ResponseEntity<Resource?> = withContext(Dispatchers.IO) {
        logger.info("Incoming frog request with token $token")
        val frog = File(frogsImages).listFiles()?.random()
        if (frog != null) {
            logger.info("Return frog image ${frog.path} for request with token $token")
            return@withContext frog.let {
                ResponseEntity.ok(FileSystemResource(it.absolutePath))
            }
        }
        logger.warn("Frog image not found!")
        return@withContext ResponseEntity.internalServerError().build()
    }

    @GetMapping("/generateToken")
    suspend fun generateToken(@RequestHeader("Authorization") authorization: String): ResponseEntity<String> {
        val (email, password) = authorization.replace("Basic ", "").decodeBase64().split(":")
        val user = withContext(Dispatchers.IO) {
            userRepository.findOneByEmail(email)
        }
        if (user == null || user.password != password.encodeBase64()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        val newToken = tokenService.generateToken(user)
        tokenService.saveToken(newToken)
        withContext(Dispatchers.IO) {
            userRepository.saveAndFlush(user)
        }
        return ResponseEntity.ok(newToken.token)
    }

    companion object {
        private const val frogsImages = "images"
    }

}