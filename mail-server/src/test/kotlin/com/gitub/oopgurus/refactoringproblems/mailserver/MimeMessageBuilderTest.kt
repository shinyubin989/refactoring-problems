package com.gitub.oopgurus.refactoringproblems.mailserver

import com.gitub.oopgurus.refactoringproblems.mailserver.exception.MailException
import com.gitub.oopgurus.refactoringproblems.mailserver.exception.MailExceptionType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class MimeMessageBuilderTest {

    private val javaMailSender = FakeJavaMailSender(MailSendStatus.FAILURE)

    @Test
    fun `MimeMessage를 생성한다`() {
        // given
        val from = "from@naver.com"
        val to = "to@naver.com"
        val subject = "subject"
        val content = "content"

        // when
        val mimeMessageBuilder = MimeMessageBuilder(javaMailSender)
                .from(from)
                .to(to)
                .subject(subject)
                .body(content)

        // then
        assertDoesNotThrow { mimeMessageBuilder.build() }
    }

    @Test
    fun `MimeMessage를 생성할 때 예외가 발생한다`() {
        // given
        val from = ""
        val to = "to@naver.com"
        val subject = "subject"
        val content = "content"

        // when
        val mimeMessageBuilder = MimeMessageBuilder(javaMailSender)
                .from(from)
                .to(to)
                .subject(subject)
                .body(content)

        // then
        assertThat(mimeMessageBuilder.build())
                .isInstanceOf(MailException::class.java)
                .extracting("exceptionType")
                .isEqualTo(MailExceptionType.MAIL_IS_BLANK)
    }
}
