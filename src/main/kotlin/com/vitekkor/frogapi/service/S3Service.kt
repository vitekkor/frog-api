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
        logger.info("Start to upload frog images")

        val frogImages = File(frogsImages).listFiles()?.filter { it.isFile }
        val uploadedImages = minioClient.listObjects(getListObjectArgs()).map {
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

        logger.info("Not uploaded ${notUploaded?.size} frog images")

        var uploaded = 0
        notUploaded?.forEach {
            try {
                minioClient.uploadObject(it)
                uploaded++
            } catch (e: Exception) {
                logger.error("Couldn't upload file ${it.filename()}:", e)
            }
        }

        logger.info("Successfully uploaded $uploaded frog images!")

        logger.info("Start to upload iwmd images")

        val iwmdImages = File(iwmdImages).listFiles()?.filter { it.isFile }

        val uploadedIWMDImages = minioClient.listObjects(getListObjectArgs(true, iwmdPrefix)).map {
            it.get().objectName()
        }

        val notUploadedIWMD = iwmdImages?.filterNot {
            "$iwmdPrefix/" + it.name in uploadedIWMDImages
        }?.map {
            UploadObjectArgs.builder()
                .bucket(minioProperties.bucket)
                .`object`("$iwmdPrefix/" + it.name)
                .filename(it.path)
                .build()
        }

        logger.info("Not uploaded ${notUploadedIWMD?.size} frog images")

        uploaded = 0
        notUploadedIWMD?.forEach {
            try {
                minioClient.uploadObject(it)
                uploaded++
            } catch (e: Exception) {
                logger.error("Couldn't upload file ${it.filename()}:", e)
            }
        }

        logger.info("Successfully uploaded $uploaded iwmd images!")

    }

    suspend fun getRandomFrogImage(): File = withContext(Dispatchers.IO) {
        val randomImage = minioClient.listObjects(getListObjectArgs())
            .toList()
            .random().get()
        return@withContext getObject(randomImage.objectName())
    }

    suspend fun getRandomIWMDImage(): File = withContext(Dispatchers.IO) {
        val randomImage = minioClient.listObjects(getListObjectArgs(true)).filter {
            val item = it.get()
            !item.isDir && item.objectName().startsWith(iwmdPrefix)
        }.random().get()

        return@withContext getObject(randomImage.objectName())
    }

    private suspend fun getObject(name: String) = withContext(Dispatchers.IO) {
        val file = createTempFile(name.replace("/", "-"))
        val outputStream = file.outputStream()
        minioClient.getObject(
            GetObjectArgs.builder().bucket(minioProperties.bucket).`object`(name).build()
        ).use {
            it.copyTo(outputStream)
        }
        outputStream.close()
        return@withContext file.toFile().also { it.deleteOnExit() }
    }

    private fun getListObjectArgs(recursive: Boolean = false, prefix: String = "") =
        ListObjectsArgs.builder().bucket(minioProperties.bucket).recursive(recursive).prefix(prefix).build()

    companion object {
        private const val frogsImages = "images"
        private const val iwmdImages = "images/iwmd"
        private const val iwmdPrefix = "iwmd"
    }
}