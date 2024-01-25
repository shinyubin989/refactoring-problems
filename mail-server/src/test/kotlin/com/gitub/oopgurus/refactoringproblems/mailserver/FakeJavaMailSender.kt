package com.gitub.oopgurus.refactoringproblems.mailserver

import jakarta.mail.internet.MimeMessage
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.mail.javamail.MimeMessagePreparator
import java.io.InputStream

class FakeJavaMailSender(
        private val sendStatus: MailSendStatus
): JavaMailSender {

    private val javaMailSender = JavaMailSenderImpl()

    override fun send(mimeMessage: MimeMessage) {
        if(sendStatus == MailSendStatus.FAILURE) {
            throw Exception()
        }
    }

    override fun send(vararg mimeMessages: MimeMessage?) {
        TODO("Not yet implemented")
    }

    override fun send(mimeMessagePreparator: MimeMessagePreparator) {
        TODO("Not yet implemented")
    }

    override fun send(vararg mimeMessagePreparators: MimeMessagePreparator?) {
        TODO("Not yet implemented")
    }

    override fun send(simpleMessage: SimpleMailMessage) {
        TODO("Not yet implemented")
    }

    override fun send(vararg simpleMessages: SimpleMailMessage?) {
        TODO("Not yet implemented")
    }

    override fun createMimeMessage(): MimeMessage {
        return javaMailSender.createMimeMessage()
    }

    override fun createMimeMessage(contentStream: InputStream): MimeMessage {
        return javaMailSender.createMimeMessage(contentStream)
    }
}
