package com.gitub.oopgurus.refactoringproblems.mailserver.exception

class MailException(
        private val exceptionType: MailExceptionType,
) : ApplicationException() {
    override fun exceptionType(): ApplicationExceptionType {
        return exceptionType
    }
}
