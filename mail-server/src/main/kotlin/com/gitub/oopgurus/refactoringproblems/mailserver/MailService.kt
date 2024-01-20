package com.gitub.oopgurus.refactoringproblems.mailserver

import com.fasterxml.jackson.databind.ObjectMapper
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
        val from = EmailAddress(sendMailDto.fromAddress, sendMailDto.fromName)
        val to = EmailAddress(sendMailDto.toAddress)
        val subject = EmailSubject(sendMailDto.title)
        val body = EmailBody(html)

        val mimeMessageBuilder = MimeMessageBuilder(javaMailSender, from, to, subject, body)
        if(files.isNotEmpty()) { mimeMessageBuilder.files(files) }
        val mimeMessage = mimeMessageBuilder.build()

        val mail = Mail(javaMailSender, mailRepository, objectMapper, mailSpamService)
        sendMailDto.sendAfterSeconds?.let { mail.afterSeconds(it) }
        mail.send(mimeMessage, sendMailDto)
    }
}
