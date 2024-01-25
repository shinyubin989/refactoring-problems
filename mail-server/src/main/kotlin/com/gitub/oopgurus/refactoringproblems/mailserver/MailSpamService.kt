package com.gitub.oopgurus.refactoringproblems.mailserver

import com.gitub.oopgurus.refactoringproblems.mailserver.exception.MailExceptionType
import com.gitub.oopgurus.refactoringproblems.mailserver.exception.MailException
import mu.KotlinLogging
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component


@Component
class MailSpamService(
    private val mailRepository: MailRepository,
) {
    private val log = KotlinLogging.logger {}

    fun needBlockByDomainName(recipient: String): Result<Unit> {
        return when (recipient) {
            in "naver.com" -> {
                Result.success(Unit)
            }
            in "gmail.com" -> {
                Result.success(Unit)
            }
            else -> {
                Result.failure(MailException(MailExceptionType.BLOCKED_DOMAIN))
            }
        }
    }

    fun needBlockByRecentSuccess(recipient: String): Result<Unit> {
        val needBlock = mailRepository.findByToAddressOrderByCreatedAt(recipient, Pageable.ofSize(3)).let {
            it.all { it.isSuccess.not() } && it.size == 3
        }
        return if (needBlock) {
            Result.failure(MailException(MailExceptionType.BLOCKED_MAIL_BY_RECENT_FAILURE))
        } else {
            Result.success(Unit)
        }
    }
}
