package com.gitub.oopgurus.refactoringproblems.mailserver.exception

import org.springframework.http.HttpStatus

interface ApplicationExceptionType {

    fun errorCode(): String
    fun errorMessage(): String
    fun httpStatus(): HttpStatus
}
