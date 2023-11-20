package com.vandenbreemen.ktt.view

import com.vandenbreemen.ktt.interactor.MarkdownInteractor
import com.vandenbreemen.ktt.model.Page
import com.vandenbreemen.ktt.persistence.SQLiteWikiRepository
import com.vandenbreemen.ktt.view.plugins.PageLinkPlugin
import com.vandenbreemen.ktt.view.plugins.TableOfContentsPlugin
import org.amshove.kluent.shouldContain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

internal class PageRenderingInteractorTest() {

    val registry = PageRenderingPluginRegistry()
    val repository = SQLiteWikiRepository("page_plugin_tests.dat")
    val pageRenderingInteractor = PageRenderingInteractor(MarkdownInteractor(), registry)

    @BeforeEach
    fun setup() {
        registry.register(PageLinkPlugin(repository))
        registry.register(TableOfContentsPlugin())
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
    fun `should translate page links for existing pages`() {
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

    @Test
    fun `should not support question marks in page links`() {
        //  Arrange
        repository.createPage(Page("Test Page", "This is a test"))
        val markdown =
            """
## Here is a Test
This test contains a link to a [Test Page?] that you can try out.
""".trimIndent()

        val rendered = pageRenderingInteractor.render(Page("test", markdown))

        println(rendered)

        rendered.shouldContain("[Test Page?]")
    }

    //  Leave these for any implementation of markdown that supports the extended specification that includes footnotes
    //  see also https://www.markdownguide.org/extended-syntax/#footnotes
    //  TODO    for later
    //@Test
    fun `should ignore links that begin with carets`() {
        val markdown =
            """
Here's a simple footnote,[^1] and here's a longer one.[^bignote]

[^1]: This is the first footnote.

[^bignote]: Here's one with multiple paragraphs and code.

    Indent paragraphs to include them in the footnote.

    `{ my code }`

    Add as many paragraphs as you like.
""".trimIndent()

        val rendered = pageRenderingInteractor.render(Page("test", markdown))

        println(rendered)

        rendered.shouldContain("<a href=\"/page/1\">Test Page</a>")
    }

    @Test
    fun `should create page links that include periods`() {
        //  Arrange
        repository.createPage(Page("Test Page", "This is a test"))
        val markdown =
            """
## Here is a Test
This test contains a link to a [Test.Page] that you can try out.
""".trimIndent()

        val rendered = pageRenderingInteractor.render(Page("test", markdown))

        println(rendered)

        rendered.shouldContain("<a href=\"/page/create/Test.Page\">Test.Page</a>")
    }

    @Test
    fun `should create page links that contain brackets`() {
        repository.createPage(Page("Test Page", "This is a test"))
        val markdown =
            """
## Here is a Test
This test contains a link to a [Test.Page(num)] that you can try out.
""".trimIndent()

        val rendered = pageRenderingInteractor.render(Page("test", markdown))

        println(rendered)

        rendered.shouldContain("<a href=\"/page/create/Test.Page(num)\">Test.Page(num)</a>")
    }

    @Test
    fun `should create page links that contain dashes`() {
        repository.createPage(Page("Test Page", "This is a test"))
        val markdown =
            """
## Here is a Test
This test contains a link to a [TICK-1234] that you can try out.
""".trimIndent()

        val rendered = pageRenderingInteractor.render(Page("test", markdown))

        println(rendered)

        rendered.shouldContain("<a href=\"/page/create/TICK-1234\">TICK-1234</a>")
    }

    @Test
    fun `should ignore alt texts for embedded images while creating links`() {
        repository.createPage(Page("Test Page", "This is a test"))
        val markdown =
            """
## Here is a Test
This page contains ![test image](an/image)
What do you think?
""".trimIndent()

        val rendered = pageRenderingInteractor.render(Page("test", markdown))

        println(rendered)

        rendered.shouldContain("<img src=\"an/image\" alt=\"test image\" />")
    }

    @Test
    fun `should properly render bullet points with links`() {
        //  Arrange
        repository.createPage(Page("Test Page", "This is a test"))
        val markdown =
            """
## Here is a Test
This test contains a link to a link that you can try out.
* [Test.Page]
""".trimIndent()

        val rendered = pageRenderingInteractor.render(Page("test", markdown))

        println(rendered)

        rendered.shouldContain("<li><a href=\"/page/create/Test.Page\">Test.Page</a>")
    }

    @Test
    fun `should properly render bullet points with links for existing pages`() {
        //  Arrange
        repository.createPage(Page("Test Page", "This is a test"))
        val markdown =
            """
## Here is a Test
This test contains a link to a link that you can try out.
* [Test.Page]
""".trimIndent()
        repository.createPage(Page("Test.Page", "This is a test"))
        val rendered = pageRenderingInteractor.render(Page("test", markdown))

        println(rendered)

        rendered.shouldContain("<li><a href=\"/page/2\">Test.Page</a>")
    }

    @Test
    fun `should properly render a page whose content is a single link`() {
        repository.createPage(Page("Test Page", "This is a test"))
        val markdown =
            """
[Test.Page]
""".trimIndent()
        repository.createPage(Page("Test.Page", "This is a test"))
        val rendered = pageRenderingInteractor.render(Page("test", markdown))

        println(rendered)

        rendered.shouldContain("<a href=\"/page/2\">Test.Page</a>")
    }

}