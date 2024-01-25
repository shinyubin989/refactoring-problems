package com.gitub.oopgurus.refactoringproblems.mailserver.exception

import org.springframework.http.HttpStatus

enum class TemplateExceptionType (
        val errorCode: String,
        val errorMessage: String,
        val httpStatus: HttpStatus
) : ApplicationExceptionType {

    TEMPLATE_NOT_FOUND(
            errorCode = "TE01",
            errorMessage = "템플릿을 찾을 수 없습니다.",
            httpStatus = HttpStatus.NOT_FOUND
    ),

    ;

    override fun errorCode(): String {
        return errorCode
    }

    override fun errorMessage(): String {
        return errorMessage
    }

    override fun httpStatus(): HttpStatus {
        return httpStatus
    }
}
