package com.gitub.oopgurus.refactoringproblems.mailserver

import java.lang.IllegalArgumentException

data class EmailAddress(
        val address: String,
        val name: String? = null
) {
    init {
        if(address.isBlank()) throw RuntimeException("address must not be blank")
        Regex(".+@.*\\..+").matches(address).let {
            if (it.not()) {
                throw IllegalArgumentException("이메일 형식 오류")
            }
        }
    }
}
