package com.gitub.oopgurus.refactoringproblems.mailserver

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.mail.internet.InternetAddress
import org.springframework.http.HttpMethod
import org.springframework.http.client.ClientHttpResponse
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component
import org.springframework.util.StreamUtils
import org.springframework.util.unit.DataSize
import org.springframework.web.client.RestTemplate
import java.io.File
import java.io.FileOutputStream


@Component
class MailService(
        private val javaMailSender: JavaMailSender,
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
        val files = convertToFile(sendMailDto.fileAttachments)

        val from = InternetAddress(sendMailDto.fromAddress, sendMailDto.fromName, "UTF-8")
        val to = InternetAddress(sendMailDto.toAddress)
        val mimeMessage = MimeMessageBuilder(javaMailSender, from, to, sendMailDto.title, html)
                .files(files)
                .build()

        val  mail = Mail(javaMailSender, mailRepository, objectMapper)
        sendMailDto.sendAfterSeconds?.let { mail.afterSeconds(it) }
        mail.send(mimeMessage, sendMailDto)
    }
}
