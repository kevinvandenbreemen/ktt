package com.vandenbreemen.ktt.interactor

import com.vandenbreemen.ktt.model.StylesheetType
import com.vandenbreemen.ktt.persistence.SQLiteWikiRepository
import org.amshove.kluent.shouldContain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.io.File

internal class CustomCssInteractorTest {

    val path = "cust_css.dat"
    val customCssInteractor = CustomCssInteractor(SQLiteWikiRepository(path))

    @AfterEach
    fun tearDown() {
        File(path).delete()
    }

    @Test
    fun `should store css for content type`() {

        val expectedCss = """
.wiki_entry div {
    background: blue;
}
        """.trimIndent()

        customCssInteractor.storeStylesheet(StylesheetType.WikiEntry, """
.wiki_entry div {
    background: blue;
}
        """.trimIndent())


        val css = customCssInteractor.getCss()
        css.shouldContain(expectedCss)
    }

    @Test
    fun `should load css for specific content type`() {

        val expectedCss = """
.wiki_entry div {
    background: blue;
}
        """.trimIndent()

        customCssInteractor.storeStylesheet(StylesheetType.WikiEntry, """
.wiki_entry div {
    background: blue;
}
        """.trimIndent())


        val css = customCssInteractor.getCssForType(StylesheetType.WikiEntry)
        css.shouldContain(expectedCss)
    }

}