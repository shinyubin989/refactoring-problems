package com.gitub.oopgurus.refactoringproblems.mailserver.exception

abstract class ApplicationException : RuntimeException() {

    abstract fun exceptionType(): ApplicationExceptionType
}
