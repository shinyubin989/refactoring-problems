package com.gitub.oopgurus.refactoringproblems.mailserver.exception

class MailException(
        private val mailExceptionType: MailExceptionType,
) : ApplicationException() {
    override fun exceptionType(): ApplicationExceptionType {
        return mailExceptionType
    }
}
