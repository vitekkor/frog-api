package com.vitekkor.frogapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@EnableConfigurationProperties
@ConfigurationPropertiesScan
@SpringBootApplication
class FrogApiApplication

fun main(args: Array<String>) {
    runApplication<FrogApiApplication>(*args)
}