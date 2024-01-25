package com.gitub.oopgurus.refactoringproblems.mailserver.exception

class TemplateException(
        private val templateExceptionType: TemplateExceptionType
) : ApplicationException() {

    override fun exceptionType(): ApplicationExceptionType {
        return templateExceptionType
    }
}
