package com.gitub.oopgurus.refactoringproblems.mailserver

import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import jakarta.mail.internet.MimeUtility
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import java.io.File

class MimeMessageBuilder(
        private val javaMailSender: JavaMailSender,
        private val from: EmailAddress,
        private val to: EmailAddress,
        private val emailSubject: EmailSubject,
        private val emailBody: EmailBody,
) {

    private val mimeMessage = javaMailSender.createMimeMessage()
    private val mimeMessageHelper = MimeMessageHelper(mimeMessage, true, "UTF-8")
    var postfixTitle = ""

    fun files(files: List<File>): MimeMessageBuilder {
        files.forEach {
            mimeMessageHelper.addAttachment(
                    MimeUtility.encodeText(
                            it.name,
                            "UTF-8",
                            "B"
                    ), it
            )
        }
        if (files.isNotEmpty()) {
            val totalSize = files
                    .map { it.totalSpace }
                    .reduceOrNull { acc, size -> acc + size } ?: 0
            postfixTitle = " (첨부파일 [${files.size}]개, 전체크기 [$totalSize bytes])"
        }
        return this
    }

    fun build(): MimeMessage {
        val fromAddress = InternetAddress(from.address, from.name)
        val toAddress = InternetAddress(to.address, to.name)
        val subject = emailSubject.addPostfix(postfixTitle).subject

        mimeMessageHelper.setFrom(fromAddress)
        mimeMessageHelper.setTo(toAddress)
        mimeMessageHelper.setSubject(MimeUtility.encodeText(subject, "UTF-8", "B")) // Base64 encoding
        mimeMessageHelper.setText(emailBody.body, true)
        return mimeMessage
    }
}
