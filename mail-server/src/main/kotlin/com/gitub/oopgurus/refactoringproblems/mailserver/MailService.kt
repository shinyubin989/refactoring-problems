package com.gitub.oopgurus.refactoringproblems.mailserver

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.mail.internet.InternetAddress
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component

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

        val html = mailTemplate.assembleHtmlMailTemplate(sendMailDto.htmlTemplateName, sendMailDto.htmlTemplateParameters)
        val files = convertToFile(sendMailDto.fileAttachments)

        val from = InternetAddress(sendMailDto.fromAddress, sendMailDto.fromName, "UTF-8")
        val to = InternetAddress(sendMailDto.toAddress)
        val mimeMessage = MimeMessageBuilder(javaMailSender, from, to, sendMailDto.title, html)
                .files(files)
                .build()

        val  mail = Mail(javaMailSender, mailRepository, objectMapper, mailSpamService)
        sendMailDto.sendAfterSeconds?.let { mail.afterSeconds(it) }
        mail.send(mimeMessage, sendMailDto)
    }
}
