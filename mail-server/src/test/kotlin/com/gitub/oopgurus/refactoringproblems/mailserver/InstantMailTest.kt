package com.gitub.oopgurus.refactoringproblems.mailserver

import jakarta.mail.internet.MimeMessage
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.mail.javamail.MimeMessageHelper
import java.time.LocalDateTime

class InstantMailTest {

    private val saveJob = { status: MailSendStatus ->
        mailRepository.save(MailEntity(
                title = "title",
                fromAddress = "from@gmail.com",
                fromName = "fromName",
                toAddress = "toAddress",
                htmlTemplateName = "htmlTemplateName",
                htmlTemplateParameters = "parameters",
                isSuccess = status.isSuccess,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
        ))
    }

    private var javaMailSender = FakeJavaMailSender(MailSendStatus.SUCCESS)
    private lateinit var mailRepository: MailRepository
    private lateinit var mimeMessage: MimeMessage
    private val instantMail = InstantMail(javaMailSender) { status ->
        saveJob(status)
    }

    @BeforeEach
    fun setUp() {
        mailRepository = FakeMailRepository()

        mimeMessage = javaMailSender.createMimeMessage()
        val mimeMessageHelper = MimeMessageHelper(mimeMessage, true, "UTF-8")
        mimeMessageHelper.setFrom("from@gmail.com")
        mimeMessageHelper.setTo("to@gmail.com")
        mimeMessageHelper.setSubject("title")
        mimeMessageHelper.setText("text", true)
    }

    @Test
    fun `메일을 보낸 후 레포지터리에 저장한다`() {
        // given
        val beforeSize = mailRepository.findAll().size

        // when
        instantMail.send(mimeMessage)

        // then
        val mail = mailRepository.findAll()
        assertAll(
                { assert(mail.size == beforeSize + 1) },
                { assert(mail.last().isSuccess) }
        )
    }

    @Test
    fun `메일을 보내는데 실패하면 레포지터리에 false로 저장한다`() {
        // given
        javaMailSender = FakeJavaMailSender(MailSendStatus.FAILURE)
        val instantMail = InstantMail(javaMailSender) { status ->
            saveJob(status)
        }
        val beforeSize = mailRepository.findAll().size

        // when
        instantMail.send(mimeMessage)

        // then
        val mail = mailRepository.findAll()
        assertAll(
                { assert(mail.size == beforeSize + 1) },
                { assert(mail.last().isSuccess.not()) }
        )
    }
}
