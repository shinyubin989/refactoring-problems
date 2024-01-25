package com.gitub.oopgurus.refactoringproblems.mailserver.exception

data class ApplicationExceptionResponse(
        val errorCode: String,
        val errorMessage: String
){
    companion object {
        fun from(exceptionType: ApplicationExceptionType): ApplicationExceptionResponse {
            return ApplicationExceptionResponse(
                    errorCode = exceptionType.errorCode(),
                    errorMessage = exceptionType.errorMessage()
            )
        }
    }
}
