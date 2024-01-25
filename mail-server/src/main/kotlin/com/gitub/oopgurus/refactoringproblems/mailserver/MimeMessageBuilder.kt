package com.gitub.oopgurus.refactoringproblems.mailserver

import com.gitub.oopgurus.refactoringproblems.mailserver.exception.MailException
import com.gitub.oopgurus.refactoringproblems.mailserver.exception.MailExceptionType.*
import jakarta.mail.internet.MimeMessage
import jakarta.mail.internet.MimeUtility
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import java.io.File

class MimeMessageBuilder(
        private val javaMailSender: JavaMailSender,
) {

    private val mimeMessage = javaMailSender.createMimeMessage()
    private val mimeMessageHelper = MimeMessageHelper(mimeMessage, true, "UTF-8")
    var postfixTitle = ""

    private var from: Result<String> = Result.failure(MailException(MAIL_IS_BLANK))
    private var to: Result<String> = Result.failure(MailException(MAIL_IS_BLANK))
    private var subject: Result<String> = Result.failure(MailException(MAIL_SUBJECT_IS_BLANK))
    private var body: Result<String> = Result.failure(MailException(MAIL_BODY_IS_BLANK))

    fun from(from: String): MimeMessageBuilder {
        Regex(".+@.*\\..+").matches(from).let {
            if (it.not()) {
                this.from = Result.failure(MailException(INVALID_MAIL))
            } else {
                this.from = Result.success(from)
            }
        }
        return this
    }

    fun to(to: String): MimeMessageBuilder {
        Regex(".+@.*\\..+").matches(to).let {
            if (it.not()) {
                this.to = Result.failure(MailException(INVALID_MAIL))
            } else {
                this.to = Result.success(to)
            }
        }
        return this
    }

    fun subject(subject: String): MimeMessageBuilder {
        if (subject.isBlank()) {
            this.subject = Result.failure(MailException(MAIL_SUBJECT_IS_BLANK))
        } else {
            this.subject = Result.success(subject)
        }
        return this
    }

    fun body(body: String): MimeMessageBuilder {
        if (body.isBlank()) {
            this.body = Result.failure(MailException(MAIL_BODY_IS_BLANK))
        } else {
            this.body = Result.success(body)
        }
        return this
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
        if (files.isNotEmpty()) {
            val totalSize = files
                    .map { it.totalSpace }
                    .reduceOrNull { acc, size -> acc + size } ?: 0
            postfixTitle = " (첨부파일 [${files.size}]개, 전체크기 [$totalSize bytes])"
        }
        return this
    }

    fun build(): MimeMessage {
        mimeMessageHelper.setFrom(from.getOrThrow())
        mimeMessageHelper.setTo(to.getOrThrow())
        mimeMessageHelper.setSubject(MimeUtility.encodeText(subject.getOrThrow(), "UTF-8", "B")) // Base64 encoding
        mimeMessageHelper.setText(body.getOrThrow(), true)
        return mimeMessage
    }
}
