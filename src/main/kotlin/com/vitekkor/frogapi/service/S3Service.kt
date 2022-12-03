package com.vitekkor.frogapi.service

import com.vitekkor.frogapi.configuration.properties.MinioProperties
import io.minio.GetObjectArgs
import io.minio.ListObjectsArgs
import io.minio.MinioClient
import io.minio.UploadObjectArgs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KotlinLogging.logger
import org.springframework.stereotype.Service
import java.io.File
import javax.annotation.PostConstruct
import kotlin.io.path.createTempFile
import kotlin.io.path.outputStream

@Service
class S3Service(private val minioClient: MinioClient, private val minioProperties: MinioProperties) {
    private val logger = logger {}

    @PostConstruct
    fun initS3() {
        val frogImages = File(frogsImages).listFiles()
        val uploadedImages =
            minioClient.listObjects(ListObjectsArgs.builder().bucket(minioProperties.bucket).build()).map {
                it.get().objectName()
            }
        val notUploaded = frogImages?.filterNot {
            it.name in uploadedImages
        }?.map {
            UploadObjectArgs.builder()
                .bucket(minioProperties.bucket)
                .`object`(it.name)
                .filename(it.path)
                .build()
        }
        notUploaded?.forEach {
            try {
                minioClient.uploadObject(it)
            } catch (e: Exception) {
                logger.error("Couldn't upload file ${it.filename()}:", e)
            }
        }

    }

    suspend fun getRandomFrogImage(): File = withContext(Dispatchers.IO) {
        val randomImage = minioClient.listObjects(ListObjectsArgs.builder().bucket(minioProperties.bucket).build())
            .toList()
            .random().get()
        val file = createTempFile(randomImage.objectName())
        val outputStream = file.outputStream()
        minioClient.getObject(GetObjectArgs.builder().bucket(minioProperties.bucket).`object`(randomImage.objectName()).build()).use {
            it.copyTo(outputStream)
        }
        outputStream.close()
        return@withContext file.toFile().also { it.deleteOnExit() }
    }

    companion object {
        private const val frogsImages = "images"
    }
}