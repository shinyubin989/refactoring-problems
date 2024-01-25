package com.gitub.oopgurus.refactoringproblems.mailserver

import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class MailRepositoryImpl(
        private val mailJpaRepository: MailJpaRepository
) : MailRepository {

    override fun findByToAddressOrderByCreatedAt(recipient: String, pageable: Pageable): List<MailEntity> {
        return mailJpaRepository.findByToAddressOrderByCreatedAt(recipient, pageable)
    }

    override fun save(mailEntity: MailEntity): MailEntity {
        return mailJpaRepository.save(mailEntity)
    }

    override fun findAll(): List<MailEntity> {
        return mailJpaRepository.findAll()
    }
}
