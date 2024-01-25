package com.gitub.oopgurus.refactoringproblems.mailserver.exception

class TemplateException(
        private val exceptionType: TemplateExceptionType
) : ApplicationException() {

    override fun exceptionType(): ApplicationExceptionType {
        return exceptionType
    }
}
