package com.vitekkor.frogapi.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("minio")
data class MinioProperties(
    val endpoint: String,
    val accessKey: String,
    val secretKey: String,
    val bucket: String,
    val autoCreation: Boolean
)
