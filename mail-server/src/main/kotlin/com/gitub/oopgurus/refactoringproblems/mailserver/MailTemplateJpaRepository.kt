package com.gitub.oopgurus.refactoringproblems.mailserver

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MailTemplateJpaRepository : JpaRepository<MailTemplateEntity, Long> {

    fun findByName(name: String): MailTemplateEntity?
}
