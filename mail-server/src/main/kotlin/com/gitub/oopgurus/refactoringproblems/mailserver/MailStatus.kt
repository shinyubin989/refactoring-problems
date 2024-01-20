package com.gitub.oopgurus.refactoringproblems.mailserver

interface MailStatus {

    fun save(sendMailDto: SendMailDto): MailEntity
}
