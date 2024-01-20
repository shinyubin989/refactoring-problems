package com.gitub.oopgurus.refactoringproblems.mailserver

import org.springframework.http.HttpMethod
import org.springframework.http.client.ClientHttpResponse
import org.springframework.util.StreamUtils
import org.springframework.util.unit.DataSize
import org.springframework.web.client.RestTemplate
import java.io.File
import java.io.FileOutputStream

fun convertToFile(files: List<FileAttachment>): List<File> {
    val restTemplate = RestTemplate()
    val fileResults = files.mapIndexed { index, file ->
        val result = restTemplate.execute(
                file.url,
                HttpMethod.GET,
                null,
                { clientHttpResponse: ClientHttpResponse ->
                    val id = "file-${index}-${java.util.UUID.randomUUID()}"
                    val tempFile = File.createTempFile(id, "")
                    StreamUtils.copy(clientHttpResponse.body, FileOutputStream(tempFile))

                    if (tempFile.length() != clientHttpResponse.headers.contentLength) {
                        throw RuntimeException("파일 크기 불일치")
                    }
                    if (DataSize.ofKilobytes(2048) <= DataSize.ofBytes(clientHttpResponse.headers.contentLength)) {
                        throw RuntimeException("파일 크기 초과")
                    }
                    tempFile
                }
        )
        result ?: throw RuntimeException("파일 초기화 실패")
        result
    }
    return fileResults

}
