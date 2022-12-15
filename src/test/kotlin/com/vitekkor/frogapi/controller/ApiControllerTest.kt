package com.vitekkor.frogapi.controller

import com.vitekkor.frogapi.configuration.TestConfiguration
import com.vitekkor.frogapi.configuration.properties.MinioProperties
import com.vitekkor.frogapi.db.entity.Token
import com.vitekkor.frogapi.db.entity.User
import com.vitekkor.frogapi.db.repository.UserRepository
import com.vitekkor.frogapi.extension.any
import com.vitekkor.frogapi.service.S3Service
import com.vitekkor.frogapi.service.TokenService
import com.vitekkor.frogapi.util.encodeBase64
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.io.File
import java.util.Calendar
import kotlin.io.path.createTempFile
import kotlin.test.assertTrue


@RunWith(PowerMockRunner::class)
@PrepareForTest(value = [Calendar::class])
@WebMvcTest(ApiController::class)
@EnableConfigurationProperties(MinioProperties::class)
@Import(value = [TestConfiguration::class])
class ApiControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @SpyBean
    private lateinit var apiController: ApiController

    @SpyBean
    private lateinit var userRepository: UserRepository

    @SpyBean
    private lateinit var tokenService: TokenService

    @SpyBean
    private lateinit var s3Service: S3Service

    private val notWednesdayCalendar = Calendar.getInstance().apply {
        setWeekDate(get(Calendar.YEAR), get(Calendar.WEEK_OF_YEAR), Calendar.FRIDAY)
    }

    private val wednesdayCalendar = Calendar.getInstance().apply {
        setWeekDate(get(Calendar.YEAR), get(Calendar.WEEK_OF_YEAR), Calendar.WEDNESDAY)
    }

    @Test
    fun getFrogTest() {
        runBlocking {
            val expectedFrog = File("images/1jxIU8E.jpg")
            val frogImage = expectedFrog.copyTo(createTempFile("frog.test").toFile(), true)

            `when`(tokenService.saveToken(any())).thenAnswer { }
            doReturn(Token()).`when`(tokenService).getToken("token")
            doReturn(frogImage).`when`(s3Service).getRandomFrogImage()
            PowerMockito.mockStatic(Calendar::class.java)
            `when`(Calendar.getInstance()).thenReturn(notWednesdayCalendar)

            val result = mockMvc.perform(get("/api/v1/token/frog"))
                .andExpect(request().asyncStarted())
                .andReturn()
            mockMvc.perform(asyncDispatch(result))
                .andExpect(status().`is`(200))
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                .andDo {
                    assertTrue(expectedFrog.readBytes().contentEquals(it.response.contentAsByteArray))
                }
        }
    }

    @Test
    fun iwmdModeTest() {
        runBlocking {
            val expectedFrog = File("images/iwmd/iwmd.jpg")
            val frogImage = expectedFrog.copyTo(createTempFile("frog.test").toFile(), true)

            `when`(tokenService.saveToken(any())).thenAnswer { }
            doReturn(Token()).`when`(tokenService).getToken("token")
            doReturn(frogImage).`when`(s3Service).getRandomFrogImage()
            PowerMockito.mockStatic(Calendar::class.java)
            mockStatic(Calendar::class.java)
            `when`(Calendar.getInstance()).thenReturn(wednesdayCalendar)

            val result = mockMvc.perform(get("/api/v1/token/frog"))
                .andExpect(request().asyncStarted())
                .andReturn()
            mockMvc.perform(asyncDispatch(result))
                .andExpect(status().`is`(200))
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                .andDo {
                    assertTrue(expectedFrog.readBytes().contentEquals(it.response.contentAsByteArray))
                }
        }
    }

    @Test
    fun notAuthorizedTest() {
        runBlocking {
            doReturn(null).`when`(tokenService).getToken("token")
            val result = mockMvc.perform(get("/api/v1/token/frog"))
                .andExpect(request().asyncStarted())
                .andReturn()
            mockMvc.perform(asyncDispatch(result))
                .andExpect(status().`is`(401))
        }
    }

    @Test
    fun generateTokenTest() {
        runBlocking {
            doReturn(User(1, "Viktor", "vitekkor@ya.ru", "qwerty".encodeBase64())).`when`(userRepository)
                .findOneByEmail("vitekkor@ya.ru")
            doReturn(Token(1, "TOKEN")).`when`(tokenService).generateToken(any())

            var result = mockMvc.perform(get("/api/v1/generateToken"))
                .andExpect(request().asyncStarted()).andReturn()

            mockMvc.perform(asyncDispatch(result))
                .andExpect(status().`is`(401))

            result = mockMvc.perform(
                get("/api/v1/generateToken")
                    .header(HttpHeaders.AUTHORIZATION, "vitekkor@ya.ru:123456".encodeBase64())
            )
                .andExpect(request().asyncStarted())
                .andReturn()
            mockMvc.perform(asyncDispatch(result))
                .andExpect(status().`is`(401))

            result = mockMvc.perform(
                get("/api/v1/generateToken")
                    .header(HttpHeaders.AUTHORIZATION, "vitekkor@ya.ru:qwerty".encodeBase64())
            )
                .andExpect(request().asyncStarted())
                .andReturn()
            mockMvc.perform(asyncDispatch(result))
                .andExpect(status().`is`(200))
                .andExpect(content().string("TOKEN"))
        }
    }


}