package com.gitub.oopgurus.refactoringproblems.mailserver

import com.github.jknack.handlebars.Handlebars
import com.gitub.oopgurus.refactoringproblems.mailserver.exception.TemplateException
import com.gitub.oopgurus.refactoringproblems.mailserver.exception.TemplateExceptionType
import com.gitub.oopgurus.refactoringproblems.mailserver.exception.TemplateExceptionType.HTML_BODY_IS_NULL
import com.gitub.oopgurus.refactoringproblems.mailserver.exception.TemplateExceptionType.TEMPLATE_NAME_IS_BLANK
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

class MailTemplateTest {

    lateinit var mailRepository: MailTemplateRepository
    lateinit var mailTemplate: MailTemplate

    @BeforeEach
    fun setUp() {
        mailRepository = FakeMailTemplateRepository()
        mailTemplate = MailTemplate(mailRepository, Handlebars())
    }

    @Test
    fun `템플릿을 생성한다`() {
        // given
        val createMailTemplateDtos = listOf(
                CreateMailTemplateDto(
                        name = "name1",
                        htmlBody = "htmlBody1"
                ),
                CreateMailTemplateDto(
                        name = "name2",
                        htmlBody = "htmlBody2"
                )
        )

        // when
        val result = mailTemplate.create(createMailTemplateDtos)

        // then
        assertAll(
                { assertThat(result.isSuccess).isTrue() },
                { assertThat(result.isFailure).isFalse() },
                { assertThat(result.getOrNull()).isEqualTo(Unit) }
        )
    }

    @Test
    fun `htmlBody가 비어있으면 템플릿 생성에 실패한다`() {
        // given
        val createMailTemplateDtos = listOf(
                CreateMailTemplateDto(
                        name = "name1",
                        htmlBody = ""
                ),
                CreateMailTemplateDto(
                        name = "name2",
                        htmlBody = "htmlBody2"
                )
        )

        // when
        val result = mailTemplate.create(createMailTemplateDtos)

        // then
        assertAll(
                { assertThat(result.isSuccess).isFalse() },
                { assertThat(result.isFailure).isTrue() },
                {
                    assertThat(result.exceptionOrNull())
                            .isInstanceOf(TemplateException::class.java)
                            .extracting("exceptionType")
                            .isEqualTo(HTML_BODY_IS_NULL)
                },
                { assertThatThrownBy { result.getOrThrow() } }
        )
    }

    @Test
    fun `템플릿 생성시 레포지터리에 저장된다`() {
        // given
        val createMailTemplateDtos = listOf(
                CreateMailTemplateDto(
                        name = "name1",
                        htmlBody = "htmlBody1"
                ),
                CreateMailTemplateDto(
                        name = "name2",
                        htmlBody = "htmlBody2"
                )
        )

        // when
        mailTemplate.create(createMailTemplateDtos)

        // then
        assertAll(
                { assertThat(mailRepository.findByName("name1")).isNotNull() },
                { assertThat(mailRepository.findByName("name2")).isNotNull() }
        )
    }



    @Test
    fun `템플릿에 파라미터를 적용한다`() {
        // given
        val templateName = "templateName"
        val parameters = mapOf(
                "title" to "title",
                "from" to "from"
        )
        mailRepository.save(
                MailTemplateEntity(
                        name = templateName,
                        htmlBody = """
                            <html>
                            <h2>Hello {{title}} !</h2>
                            <h3>This is a sample mail from {{from}} !</h3>
                            </html>
                            """.trimIndent()
                )
        )
        val expected = """
            <html>
            <h2>Hello title !</h2>
            <h3>This is a sample mail from from !</h3>
            </html>
            """.trimIndent()

        // when
        val result = mailTemplate.assemble(templateName, parameters)

        // then
        assertAll(
                { assertThat(result.isSuccess).isTrue() },
                { assertThat(result.isFailure).isFalse() },
                { assertThat(result.getOrNull()).isEqualTo(expected) }
        )
    }

    @Test
    fun `템플릿 이름이 비어있을 경우 예외를 반환한다`() {
        // given
        val templateName = ""
        val parameters = mapOf(
                "title" to "title",
                "from" to "from"
        )

        // when
        val result = mailTemplate.assemble(templateName, parameters)

        // then
        assertAll(
                { assertThat(result.isSuccess).isFalse() },
                { assertThat(result.isFailure).isTrue() },
                {
                    assertThat(result.exceptionOrNull())
                            .isInstanceOf(TemplateException::class.java)
                            .extracting("exceptionType")
                            .isEqualTo(TEMPLATE_NAME_IS_BLANK)
                },
                { assertThatThrownBy { result.getOrThrow() } }
        )
    }

    @Test
    fun `해당하는 템플릿이 없을 경우 예외를 반환한다`() {
        // given
        val templateName = "templateName"
        val parameters = mapOf(
                "title" to "title",
                "from" to "from"
        )

        // when
        val result = mailTemplate.assemble(templateName, parameters)

        // then
        assertAll(
                { assertThat(result.isSuccess).isFalse() },
                { assertThat(result.isFailure).isTrue() },
                {
                    assertThat(result.exceptionOrNull())
                            .isInstanceOf(TemplateException::class.java)
                            .extracting("exceptionType")
                            .isEqualTo(TemplateExceptionType.TEMPLATE_NOT_FOUND)
                },
                { assertThatThrownBy { result.getOrThrow() } }
        )
    }
}
