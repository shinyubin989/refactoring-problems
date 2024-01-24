package com.gitub.oopgurus.refactoringproblems.mailserver

import jakarta.mail.internet.MimeMessage
import org.springframework.mail.javamail.JavaMailSender
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class DelayMail(
        private val javaMailSender: JavaMailSender,
        private val afterSeconds: Long,
        private val saveJob: (MailSendStatus) -> Unit
) : Mail {

    private val scheduledExecutorService = Executors.newScheduledThreadPool(10)

    override fun send(mimeMessage: MimeMessage) {
        scheduledExecutorService.schedule(
                { sendMailAndSave(mimeMessage) },
                afterSeconds,
                TimeUnit.SECONDS
        )
    }

    private fun sendMailAndSave(mimeMessage: MimeMessage) {
        val mailSendResult = try {
            javaMailSender.send(mimeMessage)
            MailSendStatus.SUCCESS
        } catch (e: Exception) {
            MailSendStatus.FAILURE
        }
        saveJob(mailSendResult)
    }
}
