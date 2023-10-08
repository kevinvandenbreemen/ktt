package com.vandenbreemen.ktt.view

import com.vandenbreemen.ktt.interactor.MarkdownInteractor
import com.vandenbreemen.ktt.model.Page
import com.vandenbreemen.ktt.view.plugins.PageLinkPlugin
import org.amshove.kluent.shouldContain
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PageRenderingInteractorTest() {

    val registry = PageRenderingPluginRegistry()
    val pageRenderingInteractor = PageRenderingInteractor(MarkdownInteractor(), registry)

    @BeforeEach
    fun setup() {
        registry.register(PageLinkPlugin())
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

}