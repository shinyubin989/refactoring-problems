package com.gitub.oopgurus.refactoringproblems.mailserver

data class EmailBody(
    val body: String
) {
    init {
        if(body.isBlank()) throw IllegalArgumentException("body must not be blank")
    }
}
