package com.gitub.oopgurus.refactoringproblems.mailserver

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.mail.internet.MimeMessage
import org.springframework.mail.javamail.JavaMailSender
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class Mail(
        private val javaMailSender: JavaMailSender,
        private val mailRepository: MailRepository,
        private val objectMapper: ObjectMapper,
        private val mailSpamService: MailSpamService
) {

    private val scheduledExecutorService = Executors.newScheduledThreadPool(10)

    private var afterSeconds: Long? = null

    fun afterSeconds(afterSeconds: Long): Mail {
        this.afterSeconds = afterSeconds
        return this
    }

    fun send(mimeMessage: MimeMessage, sendMailDto: SendMailDto) {

        validateToAddress(sendMailDto.toAddress)

        if (afterSeconds != null) {
            val tempSeconds = afterSeconds!!
            scheduledExecutorService.schedule(
                    { sendMailAndSave(mimeMessage, sendMailDto) },
                    tempSeconds,
                    TimeUnit.SECONDS
            )
        } else {
            sendMailAndSave(mimeMessage, sendMailDto)
        }
    }

    // TODO sendMailDto 제거
    private fun sendMailAndSave(mimeMessage: MimeMessage, sendMailDto: SendMailDto) {
        lateinit var mailStatus: MailStatus
        try {
            javaMailSender.send(mimeMessage)
            mailStatus = MailSuccessStatus(mailRepository, objectMapper)
        } catch (e: Exception) {
            mailStatus = MailFailureStatus(mailRepository, objectMapper)
        }
        mailStatus.save(sendMailDto)
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
