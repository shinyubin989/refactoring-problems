package com.gitub.oopgurus.refactoringproblems.mailserver

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.mail.internet.MimeMessage
import mu.KotlinLogging
import org.springframework.mail.javamail.JavaMailSender
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class Mail(
        private val javaMailSender: JavaMailSender,
        private val mailRepository: MailRepository,
        private val objectMapper: ObjectMapper
) {

    private val log = KotlinLogging.logger {}
    private val scheduledExecutorService = Executors.newScheduledThreadPool(10)

    private var afterSeconds: Long? = null

    fun afterSeconds(afterSeconds: Long): Mail {
        this.afterSeconds = afterSeconds
        return this
    }

    fun send(mimeMessage: MimeMessage, sendMailDto: SendMailDto) {
        try {
            if (afterSeconds != null) {
                scheduledExecutorService.schedule(
                        {
                            javaMailSender.send(mimeMessage)
                            mailRepository.save(
                                    MailEntity(
                                            fromAddress = mimeMessage.from.toString(),
                                            fromName = sendMailDto.fromName,
                                            toAddress = sendMailDto.toAddress,
                                            title = sendMailDto.title,
                                            htmlTemplateName = sendMailDto.htmlTemplateName,
                                            htmlTemplateParameters = objectMapper.writeValueAsString(sendMailDto.htmlTemplateParameters),
                                            isSuccess = true,
                                    )
                            )
                            log.info { "MailServiceImpl.sendMail() :: SUCCESS" }
                        },
                        afterSeconds!!,
                        TimeUnit.SECONDS
                )

            } else {
                javaMailSender.send(mimeMessage)
                mailRepository.save(
                        MailEntity(
                                fromAddress = sendMailDto.fromAddress,
                                fromName = sendMailDto.fromName,
                                toAddress = sendMailDto.toAddress,
                                title = sendMailDto.title,
                                htmlTemplateName = sendMailDto.htmlTemplateName,
                                htmlTemplateParameters = objectMapper.writeValueAsString(sendMailDto.htmlTemplateParameters),
                                isSuccess = true,
                        )
                )
                log.info { "MailServiceImpl.sendMail() :: SUCCESS" }
            }
        } catch (e: Exception) {
            mailRepository.save(
                    MailEntity(
                            fromAddress = sendMailDto.fromAddress,
                            fromName = sendMailDto.fromName,
                            toAddress = sendMailDto.toAddress,
                            title = sendMailDto.title,
                            htmlTemplateName = sendMailDto.htmlTemplateName,
                            htmlTemplateParameters = objectMapper.writeValueAsString(sendMailDto.htmlTemplateParameters),
                            isSuccess = false,
                    )
            )
            log.error(e) { "MailServiceImpl.sendMail() :: FAILED" }
        }
    }
}
