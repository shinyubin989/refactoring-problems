package com.gitub.oopgurus.refactoringproblems.mailserver

import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import jakarta.mail.internet.MimeUtility
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.util.Assert
import java.io.File

class MimeMessageBuilder(
        private val javaMailSender: JavaMailSender,
        private val from: InternetAddress,
        private val to: InternetAddress,
        private val subject: String,
        private val content: String,
) {

    private val mimeMessage = javaMailSender.createMimeMessage()
    private val mimeMessageHelper = MimeMessageHelper(mimeMessage, true, "UTF-8")

    init {
        Assert.notNull(subject, "subject must not be null")
        Assert.notNull(content, "content must not be null")

        mimeMessageHelper.setFrom(from)
        mimeMessageHelper.setTo(to)
        mimeMessageHelper.setSubject(subject)
        mimeMessageHelper.setText(content, true)
    }

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

        var postfixTitle = ""
        if (files.isNotEmpty()) {
            val totalSize = files
                    .map { it.totalSpace }
                    .reduceOrNull { acc, size -> acc + size } ?: 0
            postfixTitle = " (첨부파일 [${files.size}]개, 전체크기 [$totalSize bytes])"
        }
        mimeMessageHelper.setSubject(
                MimeUtility.encodeText(
                        subject + postfixTitle,
                        "UTF-8",
                        "B"
                )
        ) // Base64 encoding
        return this
    }


    fun build(): MimeMessage {
        return mimeMessage
    }
}
