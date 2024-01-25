package com.gitub.oopgurus.refactoringproblems.mailserver.exception

import org.springframework.http.HttpStatus

enum class MailExceptionType(
        val errorCode: String,
        val errorMessage: String,
        val httpStatus: HttpStatus
) : ApplicationExceptionType {

    MAIL_FROM_IS_BLANK(
            errorCode = "MA01",
            errorMessage = "이메일 발신자가 비어있습니다.",
            httpStatus = HttpStatus.BAD_REQUEST
    ),
    MAIL_TO_IS_BLANK(
            errorCode = "MA02",
            errorMessage = "이메일 수신자가 비어있습니다.",
            httpStatus = HttpStatus.BAD_REQUEST
    ),
    INVALID_MAIL_FROM(
            errorCode = "MA01",
            errorMessage = "이메일 발신자 형식 오류",
            httpStatus = HttpStatus.BAD_REQUEST
    ),
    INVALID_MAIL_TO(
            errorCode = "MA02",
            errorMessage = "이메일 수신자 형식 오류",
            httpStatus = HttpStatus.BAD_REQUEST
    ),
    SERVER_MAIL_SENDER_ERROR(
            errorCode = "MA03",
            errorMessage = "서버 메일 송신 오류",
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR
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
