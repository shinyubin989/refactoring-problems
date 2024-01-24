package com.gitub.oopgurus.refactoringproblems.mailserver

import jakarta.mail.internet.MimeMessage

interface Mail {
    fun send(mimeMessage: MimeMessage)
}
