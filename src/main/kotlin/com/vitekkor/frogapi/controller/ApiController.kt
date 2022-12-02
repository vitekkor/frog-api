package com.vitekkor.frogapi.controller

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KotlinLogging.logger
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.http.MediaType.IMAGE_JPEG_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.File

@RestController
@RequestMapping("/api/v1")
class ApiController {
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

    companion object {
        private const val frogsImages = "images"
    }

}