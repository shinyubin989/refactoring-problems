package com.gitub.oopgurus.refactoringproblems.mailserver

data class EmailSubject (
    val subject: String
) {
    init {
        if(subject.isBlank()) throw IllegalArgumentException("subject must not be blank")
    }

    fun addPostfix(postfix: String): EmailSubject {
        return EmailSubject(subject + postfix)
    }
}
