package com.gitub.oopgurus.refactoringproblems.mailserver

import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging

class MailFailureStatus(
        private val mailRepository: MailRepository,
        private val objectMapper: ObjectMapper,
) : MailStatus {

    private val log = KotlinLogging.logger {}

    override fun save(sendMailDto: SendMailDto): MailEntity {
        val mail = mailRepository.save(
                MailEntity(
                        fromAddress = sendMailDto.fromAddress,
                        fromName = sendMailDto.fromName,
                        toAddress = sendMailDto.toAddress,
                        title = sendMailDto.title,
                        htmlTemplateName = sendMailDto.htmlTemplateName,
                        htmlTemplateParameters = objectMapper.writeValueAsString(sendMailDto.htmlTemplateParameters),
                        isSuccess = false,
                )
        )
        log.error { "MailServiceImpl.sendMail() :: FAILED" }
        return mail
    }
}

