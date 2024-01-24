package com.gitub.oopgurus.refactoringproblems.mailserver

enum class MailSendStatus(val isSuccess: Boolean) {
    SUCCESS(true),
    FAILURE(false),
}
