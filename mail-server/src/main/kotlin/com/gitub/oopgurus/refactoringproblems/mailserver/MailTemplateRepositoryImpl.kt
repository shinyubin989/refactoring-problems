package com.gitub.oopgurus.refactoringproblems.mailserver

import org.springframework.stereotype.Repository

@Repository
class MailTemplateRepositoryImpl(
        private val mailTemplateJpaRepository: MailTemplateJpaRepository
) : MailTemplateRepository {

    override fun findByName(name: String): MailTemplateEntity? {
        return mailTemplateJpaRepository.findByName(name)
    }

    override fun save(mailTemplateEntity: MailTemplateEntity): MailTemplateEntity {
        return mailTemplateJpaRepository.save(mailTemplateEntity)
    }
}
