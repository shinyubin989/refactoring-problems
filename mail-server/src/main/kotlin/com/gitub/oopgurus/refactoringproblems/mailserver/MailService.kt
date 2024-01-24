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
        validateToAddress(to.address)
        val subject = EmailSubject(sendMailDto.title)
        val body = EmailBody(html)

        val mimeMessageBuilder = MimeMessageBuilder(javaMailSender, from, to, subject, body)
        if (files.isNotEmpty()) {
            mimeMessageBuilder.files(files)
        }
        val mimeMessage = mimeMessageBuilder.build()

        when (sendMailDto.sendAfterSeconds) {
            null -> InstantMail(javaMailSender, save(sendMailDto)).send(mimeMessage)
            else -> DelayMail(javaMailSender, sendMailDto.sendAfterSeconds, save(sendMailDto)).send(mimeMessage)
        }
    }

    fun save(sendMailDto: SendMailDto) = { status: MailSendStatus ->
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

    private fun validateToAddress(toAddress: String) {
        mailSpamService.needBlockByDomainName(toAddress).let {
            if (it) {
                throw RuntimeException("도메인 차단")
            }
        }
        mailSpamService.needBlockByRecentSuccess(toAddress).let {
            if (it) {
                throw RuntimeException("최근 메일 발송 실패로 인한 차단")
            }
        }
    }

}
