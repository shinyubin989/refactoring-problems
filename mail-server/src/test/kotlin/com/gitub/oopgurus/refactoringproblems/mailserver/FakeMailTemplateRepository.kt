package com.gitub.oopgurus.refactoringproblems.mailserver

import java.util.*
import java.util.concurrent.atomic.AtomicLong

class FakeMailTemplateRepository : MailTemplateRepository {

    private val id = AtomicLong(1)
    private val templates = mutableMapOf<Long, MailTemplateEntity>()

    override fun findByName(name: String): MailTemplateEntity? {
        return templates.values.firstOrNull { it.name == name }
    }

    override fun save(mailTemplateEntity: MailTemplateEntity): MailTemplateEntity {
        var templateId = mailTemplateEntity.id
        if (templateId == null) {
            templateId = id.getAndIncrement()
            val newTemplate = MailTemplateEntity(
                    templateId,
                    mailTemplateEntity.name,
                    mailTemplateEntity.htmlBody,
                    mailTemplateEntity.createdAt,
                    mailTemplateEntity.updatedAt
            )
            templates[templateId] = newTemplate
            return newTemplate
        }
        templates[templateId] = mailTemplateEntity
        return mailTemplateEntity
    }
}
