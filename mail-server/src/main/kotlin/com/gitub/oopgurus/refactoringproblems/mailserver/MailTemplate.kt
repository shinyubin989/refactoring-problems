package com.gitub.oopgurus.refactoringproblems.mailserver

import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Template
import org.springframework.stereotype.Component

@Component
class MailTemplate(
        private val mailTemplateRepository: MailTemplateRepository,
        private val handlebars: Handlebars
) {

    fun createMailTemplate(createMailTemplateDtos: List<CreateMailTemplateDto>) {
        createMailTemplateDtos.forEach {
            if (it.htmlBody.isBlank()) {
                throw IllegalArgumentException("htmlBody is blank")
            }
            mailTemplateRepository.save(
                    MailTemplateEntity(
                            name = it.name,
                            htmlBody = it.htmlBody,
                    )
            )
        }
    }

    fun assembleHtmlMailTemplate(templateName: String, parameters: Map<String, Any>): String {
        if (templateName.isBlank()) throw RuntimeException("템플릿 이름이 비어있습니다")

        val htmlTemplate = mailTemplateRepository.findByName(templateName)
                ?: throw RuntimeException("템플릿이 존재하지 않습니다: [${templateName}]")
        val template: Template = handlebars.compileInline(htmlTemplate.htmlBody)
        return template.apply(parameters)
    }
}
