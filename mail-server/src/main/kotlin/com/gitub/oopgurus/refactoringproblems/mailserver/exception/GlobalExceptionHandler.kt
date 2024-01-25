package com.gitub.oopgurus.refactoringproblems.mailserver.exception

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
internal class GlobalExceptionHandler {

    @ExceptionHandler(ApplicationException::class)
    fun handleApplicationException(e: ApplicationException): ResponseEntity<ApplicationExceptionResponse> {
        val exceptionType: ApplicationExceptionType = e.exceptionType()
        return ResponseEntity.status(exceptionType.httpStatus())
                .body(ApplicationExceptionResponse.from(exceptionType))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(request: HttpServletRequest, e: Exception): ResponseEntity<String> {
        return ResponseEntity.internalServerError()
                .body("Unknown error occurred. Please try later")
    }
}
