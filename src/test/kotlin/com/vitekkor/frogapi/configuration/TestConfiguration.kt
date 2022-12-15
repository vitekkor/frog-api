package com.vitekkor.frogapi.configuration

import com.vitekkor.frogapi.db.repository.TokenRepository
import com.vitekkor.frogapi.db.repository.UserRepository
import io.minio.MinioClient
import org.mockito.Mockito
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class TestConfiguration {
    @Bean
    fun userRepository(): UserRepository? {
        return Mockito.mock(UserRepository::class.java)
    }

    @Bean
    fun tokenRepository(): TokenRepository? {
        return Mockito.mock(TokenRepository::class.java)
    }

    @Bean
    fun minioClient(): MinioClient {
        return Mockito.mock(MinioClient::class.java)
    }
}