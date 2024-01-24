package com.gitub.oopgurus.refactoringproblems.mailserver

import jakarta.mail.internet.MimeMessage
import org.springframework.mail.javamail.JavaMailSender
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class Mail(
        private val javaMailSender: JavaMailSender,
        private val saveJob: (MailSendStatus) -> Unit
) {

    private val scheduledExecutorService = Executors.newScheduledThreadPool(10)

    /**
     * TODO
     */
    private var afterSeconds: Long? = null
    fun afterSeconds(afterSeconds: Long): Mail {
        this.afterSeconds = afterSeconds
        return this
    }

    fun send(mimeMessage: MimeMessage) {
        if (afterSeconds != null) {
            val tempSeconds = afterSeconds!!
            scheduledExecutorService.schedule(
                    { sendMailAndSave(mimeMessage) },
                    tempSeconds,
                    TimeUnit.SECONDS
            )
        } else {
            sendMailAndSave(mimeMessage)
        }
    }

    private fun sendMailAndSave(mimeMessage: MimeMessage) {
        val mailSendSuccess = try {
            javaMailSender.send(mimeMessage)
            MailSendStatus.SUCCESS
        } catch (e: Exception) {
            MailSendStatus.FAILURE
        }
        saveJob(mailSendSuccess)
    }
}
