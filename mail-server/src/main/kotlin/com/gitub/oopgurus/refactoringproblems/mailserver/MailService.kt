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

        val html = mailTemplate.assemble(sendMailDto.htmlTemplateName, sendMailDto.htmlTemplateParameters)
                .getOrThrow()
        val files = convertToFile(sendMailDto.fileAttachments)

        mailSpamService.needBlockByRecentSuccess(sendMailDto.toAddress)
                .onFailure { throw it }
        mailSpamService.needBlockByDomainName(sendMailDto.toAddress)
                .onFailure { throw it }

        val mimeMessage = MimeMessageBuilder(javaMailSender)
                .from(sendMailDto.fromAddress)
                .to(sendMailDto.toAddress)
                .subject(sendMailDto.title)
                .body(html)
                .files(files)
                .build()

        when (sendMailDto.sendAfterSeconds) {
            null -> InstantMail(javaMailSender, save(sendMailDto)).send(mimeMessage)
            else -> DelayMail(javaMailSender, sendMailDto.sendAfterSeconds, save(sendMailDto)).send(mimeMessage)
        }
    }

    private fun save(sendMailDto: SendMailDto) = { status: MailSendStatus ->
        val mail = mailRepository.save(
                MailEntity(
                        fromAddress = sendMailDto.fromAddress,
                        fromName = sendMailDto.fromName,
                        toAddress = sendMailDto.toAddress,
                        title = sendMailDto.title,
                        htmlTemplateName = sendMailDto.htmlTemplateName,
                        htmlTemplateParameters = objectMapper.writeValueAsString(sendMailDto.htmlTemplateParameters),
                        isSuccess = status.isSuccess,
                )
        )
    }
}
