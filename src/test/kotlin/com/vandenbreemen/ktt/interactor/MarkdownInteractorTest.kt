package com.vandenbreemen.ktt.interactor

import com.vandenbreemen.ktt.model.Page
import com.vandenbreemen.ktt.persistence.SQLiteWikiRepository
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.io.File

class MarkdownInteractorTest {

    private val file = "mkInteractorTest.dat"
    val repo = SQLiteWikiRepository(file)

    @AfterEach
    fun tearDown() {
        File(file).delete()
    }

    @Test
    fun `should detect paragraphs and translate them to html`() {
        val interactor = MarkdownInteractor()
        val html = interactor.translateToHtml("""
I like markdown.

I think I will use it in my next project.
        """.trimIndent())

        html shouldBeEqualTo "<body><p>I like markdown.</p><p>I think I will use it in my next project.</p></body>"
    }

    @Test
    fun `should detect paragraphs from data in the database and translate them to html`() {

        val interactor = MarkdownInteractor()

        val page = Page("Test Page", """
I like markdown.

I think I will use it in my next project.
        """.trimIndent())

        repo.createPage(page)
        val loaded = repo.loadPage("1")

        val html = interactor.translateToHtml(loaded.content)

        html shouldBeEqualTo "<body><p>I like markdown.</p><p>I think I will use it in my next project.</p></body>"
    }

}