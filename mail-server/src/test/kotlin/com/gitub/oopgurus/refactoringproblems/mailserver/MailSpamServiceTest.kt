package com.gitub.oopgurus.refactoringproblems.mailserver

import com.gitub.oopgurus.refactoringproblems.mailserver.exception.MailException
import com.gitub.oopgurus.refactoringproblems.mailserver.exception.MailExceptionType
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import java.time.LocalDateTime

class MailSpamServiceTest {

    private lateinit var mailRepository: MailRepository
    private lateinit var mailSpamService: MailSpamService

    @BeforeEach
    fun setUp() {
        mailRepository = FakeMailRepository()
        mailSpamService = MailSpamService(mailRepository)
    }

    @Test
    fun `도메인에 따라 차단 여부를 반환한다`() {
        // given
        val domain = "naver.com"

        // when
        val result = mailSpamService.needBlockByDomainName(domain)

        // then
        assertAll(
                { assertThat(result.isSuccess).isTrue() },
                { assertThat(result.isFailure).isFalse() }
        )
    }

    @Test
    fun `차단해야하는 도메인 이름의 경우 예외로 반환된다`() {
        // given
        val domain = "google.com"

        // when
        val result = mailSpamService.needBlockByDomainName(domain)

        // then
        assertAll(
                { assertThat(result.exceptionOrNull())
                            .isInstanceOf(MailException::class.java)
                            .extracting("exceptionType")
                            .isEqualTo(MailExceptionType.BLOCKED_DOMAIN) },
                { assertThat(result.isFailure).isTrue() },
                { assertThat(result.isSuccess).isFalse() }
        )
    }


    @Test
    fun `최근 메일 발송이 성공한 경우라면 도메인을 차단하지 않는다`() {
        // given
        val recipient = "recipient"

        // when
        val result = mailSpamService.needBlockByRecentSuccess(recipient)

        // then
        assertAll(
                { assertThat(result.isSuccess).isTrue() },
                { assertThat(result.isFailure).isFalse() }
        )
    }

    @Test
    fun `최근 3번의 메일이 모두 실패한 경우 차단 여부를 반환한다`() {
        // given
        val recipient = "recipient"
        `도메인에 대해 최근 세번의 메일이 실패하였다`(recipient)

        // when
        val result = mailSpamService.needBlockByRecentSuccess(recipient)

        // then
        assertAll(
                { assertThat(result.isSuccess).isFalse() },
                { assertThat(result.isFailure).isTrue() },
                { assertThat(result.exceptionOrNull())
                        .isInstanceOf(MailException::class.java)
                        .extracting("exceptionType")
                        .isEqualTo(MailExceptionType.BLOCKED_MAIL_BY_RECENT_FAILURE) },
        )
    }

    private fun `도메인에 대해 최근 세번의 메일이 실패하였다`(recipient: String) {
        for (i in 1..3) {
            mailRepository.save(MailEntity(i.toLong(), "title", "fromAddress", "fromName", recipient, "templateName", "parameters", false, LocalDateTime.now(), LocalDateTime.now()))
        }
    }
}
