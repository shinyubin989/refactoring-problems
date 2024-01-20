package com.gitub.oopgurus.refactoringproblems.mailserver

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.mail.internet.InternetAddress
import mu.KotlinLogging
import org.springframework.http.HttpMethod
import org.springframework.http.client.ClientHttpResponse
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import org.springframework.util.StreamUtils
import org.springframework.util.unit.DataSize
import org.springframework.web.client.RestTemplate
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


@Component
class MailService(
        private val javaMailSender: JavaMailSender,
        private val restTemplate: RestTemplate,
        private val mailRepository: MailRepository,
        private val objectMapper: ObjectMapper,
        private val mailSpamService: MailSpamService,
        private val mailTemplate: MailTemplate
) {

    fun send(sendMailDtos: List<SendMailDto>) {
        sendMailDtos.forEach {
            sendSingle(it)
        }
    }

    private fun sendSingle(sendMailDto: SendMailDto) {
        mailSpamService.needBlockByDomainName(sendMailDto.toAddress).let {
            if (it) {
                throw RuntimeException("도메인 차단")
            }
        }
        mailSpamService.needBlockByRecentSuccess(sendMailDto.toAddress).let {
            if (it) {
                throw RuntimeException("최근 메일 발송 실패로 인한 차단")
            }
        }

        val html = mailTemplate.assembleHtmlMailTemplate(sendMailDto.htmlTemplateName, sendMailDto.htmlTemplateParameters)

        val fileResults = sendMailDto.fileAttachments.mapIndexed { index, attachment ->
            val result = restTemplate.execute(
                    attachment.url,
                    HttpMethod.GET,
                    null,
                    { clientHttpResponse: ClientHttpResponse ->
                        val id = "file-${index}-${java.util.UUID.randomUUID()}"
                        val tempFile = File.createTempFile(id, "")
                        StreamUtils.copy(clientHttpResponse.body, FileOutputStream(tempFile))

                        FileAttachmentDto(
                                resultFile = tempFile,
                                name = attachment.name,
                                clientHttpResponse = clientHttpResponse
                        )
                    })

            if (result == null) {
                throw RuntimeException("파일 초기화 실패")
            }
            if (result.resultFile.length() != result.clientHttpResponse.headers.contentLength) {
                throw RuntimeException("파일 크기 불일치")
            }
            if (DataSize.ofKilobytes(2048) <= DataSize.ofBytes(result.clientHttpResponse.headers.contentLength)) {
                throw RuntimeException("파일 크기 초과")
            }
            result
        }


        val from = InternetAddress(sendMailDto.fromAddress, sendMailDto.fromName, "UTF-8")
        val to = InternetAddress(sendMailDto.toAddress)
        val mimeMessage = MimeMessageBuilder(javaMailSender, from, to, sendMailDto.title, html)
                .files(fileResults.map { it.resultFile })
                .build()

        val mail = Mail(javaMailSender, mailRepository, objectMapper)
                .send(mimeMessage, sendMailDto)

    }
}
