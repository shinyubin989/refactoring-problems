package com.gitub.oopgurus.refactoringproblems.mailserver

import org.springframework.data.domain.Pageable
import java.util.concurrent.atomic.AtomicLong

class FakeMailRepository : MailRepository {

    private val id = AtomicLong(1)
    private val mails = mutableMapOf<Long, MailEntity>()

    override fun findByToAddressOrderByCreatedAt(recipient: String, pageable: Pageable): List<MailEntity> {
        val mails = mails.values.filter { it.toAddress == recipient }.sortedBy { it.createdAt }
        if(pageable.pageSize > mails.size) {
            return mails.subList(pageable.offset.toInt(), pageable.offset.toInt() + mails.size)
        }
        return mails.subList(pageable.offset.toInt(), pageable.offset.toInt() + pageable.pageSize)
    }

    override fun save(mailEntity: MailEntity): MailEntity {
        var mailId = mailEntity.id
        if (mailId == null) {
            mailId = id.getAndIncrement()
            val newMail = MailEntity(
                    mailId,
                    mailEntity.title,
                    mailEntity.fromAddress,
                    mailEntity.fromName,
                    mailEntity.toAddress,
                    mailEntity.htmlTemplateName,
                    mailEntity.htmlTemplateParameters,
                    mailEntity.isSuccess,
                    mailEntity.createdAt,
                    mailEntity.updatedAt,

            )
            mails[mailId] = newMail
            return newMail
        }
        mails[mailId] = mailEntity
        return mailEntity
    }

    override fun findAll(): List<MailEntity> {
        return mails.values.toList()
    }
}
