package com.vitekkor.frogapi.configuration

import com.vitekkor.frogapi.configuration.properties.MinioProperties
import io.minio.BucketExistsArgs
import io.minio.MakeBucketArgs
import io.minio.MinioClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FrogApiConfiguration {
    @Bean
    fun minioClient(minioProperties: MinioProperties): MinioClient {
        val client = MinioClient.builder()
            .endpoint(minioProperties.endpoint)
            .credentials(minioProperties.accessKey, minioProperties.secretKey)
            .build()
        if (minioProperties.autoCreation) {
            val bucketExists = client.bucketExists(BucketExistsArgs.builder().bucket(minioProperties.bucket).build())
            if (!bucketExists) {
                client.makeBucket(MakeBucketArgs.builder().bucket(minioProperties.bucket).build())
            }
        }
        return client
    }

}
