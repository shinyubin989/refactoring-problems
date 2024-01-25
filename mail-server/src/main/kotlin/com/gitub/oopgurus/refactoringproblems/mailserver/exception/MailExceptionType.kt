package com.gitub.oopgurus.refactoringproblems.mailserver.exception

import org.springframework.http.HttpStatus

enum class MailExceptionType(
        val errorCode: String,
        val errorMessage: String,
        val httpStatus: HttpStatus
) : ApplicationExceptionType {

    MAIL_IS_BLANK(
            errorCode = "MA01",
            errorMessage = "이메일주소가 비어있습니다.",
            httpStatus = HttpStatus.BAD_REQUEST
    ),
    INVALID_MAIL(
            errorCode = "MA02",
            errorMessage = "이메일주소의 형식이 올바르지 않습니다.",
            httpStatus = HttpStatus.BAD_REQUEST
    ),
    SERVER_MAIL_SENDER_ERROR(
            errorCode = "MA03",
            errorMessage = "서버 메일 송신 오류",
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR
    ),
    BLOCKED_DOMAIN(
            errorCode = "MA04",
            errorMessage = "도메인 차단",
            httpStatus = HttpStatus.BAD_REQUEST
    ),
    BLOCKED_MAIL_BY_RECENT_FAILURE(
            errorCode = "MA05",
            errorMessage = "최근 메일 발송 실패로 인한 차단",
            httpStatus = HttpStatus.BAD_REQUEST
    ),
    MAIL_BODY_IS_BLANK(
            errorCode = "MA06",
            errorMessage = "메일 본문이 비어있습니다.",
            httpStatus = HttpStatus.BAD_REQUEST
    ),
    MAIL_SUBJECT_IS_BLANK(
            errorCode = "MA07",
            errorMessage = "메일 제목이 비어있습니다.",
            httpStatus = HttpStatus.BAD_REQUEST
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
