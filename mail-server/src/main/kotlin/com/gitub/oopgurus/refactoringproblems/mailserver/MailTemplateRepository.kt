package com.gitub.oopgurus.refactoringproblems.mailserver

interface MailTemplateRepository {

    fun findByName(name: String): MailTemplateEntity?

    fun save(mailTemplateEntity: MailTemplateEntity): MailTemplateEntity
}
