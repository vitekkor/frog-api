package com.vitekkor.frogapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@EnableConfigurationProperties
@ConfigurationPropertiesScan
@SpringBootApplication
class FrogApiApplication

object Dev {
    @JvmStatic
    fun main(args: Array<String>) {
        System.setProperty("spring.profiles.active", "dev")
        System.setProperty("log.dir", "frog-api/logs")

        com.vitekkor.frogapi.main(args)
    }
}

fun main(args: Array<String>) {
    runApplication<FrogApiApplication>(*args)
}