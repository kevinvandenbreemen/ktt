package com.vandenbreemen.ktt.view

import com.vandenbreemen.ktt.interactor.MarkdownInteractor
import com.vandenbreemen.ktt.model.Page
import com.vandenbreemen.ktt.persistence.SQLiteWikiRepository
import com.vandenbreemen.ktt.view.plugins.PageLinkPlugin
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldContain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

class PageRenderingInteractorTest() {

    val registry = PageRenderingPluginRegistry()
    val repository = SQLiteWikiRepository("page_plugin_tests.dat")
    val pageRenderingInteractor = PageRenderingInteractor(MarkdownInteractor(), registry)

    @BeforeEach
    fun setup() {
        registry.register(PageLinkPlugin(repository))
    }

    @AfterEach
    fun after() {
        File("page_plugin_tests.dat").delete()
    }

    @Test
    fun `should translate page links for new pages`() {

        val markdown =
"""
## Test Page

This is a test.  You can [Create a New Page] by following the link
""".trimIndent()

        val rendered = pageRenderingInteractor.render(Page("test", markdown))
        rendered.shouldContain("<a href=\"/page/create/Create%20a%20New%20Page\">Create a New Page</a>")

    }

    @Test
    fun `should translate page links for existing pages`() = runTest{
        //  Arrange
        repository.createPage(Page("Test Page", "This is a test"))
        val markdown =
"""
## Here is a Test
This test contains a link to a [Test Page] that you can try out.
""".trimIndent()

        val rendered = pageRenderingInteractor.render(Page("test", markdown))
        rendered.shouldContain("<a href=\"/page/1\">Test Page</a>")
    }

}