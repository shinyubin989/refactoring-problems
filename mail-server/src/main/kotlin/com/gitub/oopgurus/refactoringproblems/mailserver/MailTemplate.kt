package com.gitub.oopgurus.refactoringproblems.mailserver

import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Template
import com.gitub.oopgurus.refactoringproblems.mailserver.exception.TemplateException
import com.gitub.oopgurus.refactoringproblems.mailserver.exception.TemplateExceptionType.*
import org.springframework.stereotype.Component

@Component
class MailTemplate(
        private val mailTemplateRepository: MailTemplateRepository,
        private val handlebars: Handlebars
) {

    fun create(createMailTemplateDtos: List<CreateMailTemplateDto>) : Result<Unit> {
        createMailTemplateDtos.forEach {
            if (it.htmlBody.isBlank()) return Result.failure(TemplateException(HTML_BODY_IS_NULL))
            mailTemplateRepository.save(
                    MailTemplateEntity(
                            name = it.name,
                            htmlBody = it.htmlBody,
                    )
            )
        }
        return Result.success(Unit)
    }

    fun assemble(templateName: String, parameters: Map<String, Any>): Result<String> {
        if (templateName.isBlank()) return Result.failure(TemplateException(TEMPLATE_NAME_IS_BLANK))
        val htmlTemplate = mailTemplateRepository.findByName(templateName)
                ?: return Result.failure(TemplateException(TEMPLATE_NOT_FOUND))

        val template: Template = handlebars.compileInline(htmlTemplate.htmlBody)
        return Result.success(template.apply(parameters))
    }
}
