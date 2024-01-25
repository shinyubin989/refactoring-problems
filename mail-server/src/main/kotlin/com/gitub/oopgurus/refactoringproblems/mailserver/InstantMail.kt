package com.gitub.oopgurus.refactoringproblems.mailserver

import jakarta.mail.internet.MimeMessage
import org.springframework.mail.javamail.JavaMailSender

class InstantMail(
        private val javaMailSender: JavaMailSender,
        private val saveJob: (MailSendStatus) -> Unit
) : Mail {

    override fun send(mimeMessage: MimeMessage) {
        val mailSendResult = try {
            javaMailSender.send(mimeMessage)
            MailSendStatus.SUCCESS
        } catch (e: Exception) {
            MailSendStatus.FAILURE
        }
        saveJob(mailSendResult)
    }
}
