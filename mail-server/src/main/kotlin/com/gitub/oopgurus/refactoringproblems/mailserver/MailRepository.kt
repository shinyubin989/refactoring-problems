package com.gitub.oopgurus.refactoringproblems.mailserver

import org.springframework.data.domain.Pageable

interface MailRepository {

    fun findByToAddressOrderByCreatedAt(recipient: String, pageable: Pageable): List<MailEntity>

    fun save(mailEntity: MailEntity): MailEntity

    fun findAll() : List<MailEntity>
}
