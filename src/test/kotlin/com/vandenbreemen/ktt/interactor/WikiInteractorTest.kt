package com.vandenbreemen.ktt.interactor

import com.vandenbreemen.ktt.message.UserError
import com.vandenbreemen.ktt.model.Page
import com.vandenbreemen.ktt.persistence.SQLiteWikiRepository
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.`should not be null`
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

class WikiInteractorTest {

    val testWikiInteractor = TestWikiInteractor()
    val filePath = "interactor_test.db"
    val wikiInteractor = WikiInteractor(testWikiInteractor, SQLiteWikiRepository(filePath))

    @AfterEach
    fun tearDown() {
        File(filePath).delete()
    }

    @BeforeEach
    fun setup() {
        val page = Page("Test Page", "This is a test of the wiki page system")
        runBlocking { wikiInteractor.createPage(page) }
    }

    @Test
    fun `should delegate to the test wiki interactor when test page is requested`() = runTest {
        val page = wikiInteractor.fetchPage("test")
        page.`should not be null`()
    }

    @Test
    fun `should prevent editing a page with no title`() = runTest{
        val edited = Page("", "Page without a title")
        try {
            wikiInteractor.updatePage("1", edited)
            fail("Should not allow editing page if no title provided")
        } catch (e: UserError) {
            e.printStackTrace()
        }
    }

    @Test
    fun `should prevent editing a page with no content`() = runTest {
        val edited = Page("Test Page", "")
        try {
            wikiInteractor.updatePage("1", edited)
            fail("Should not allow editing page if no content provided")
        } catch (e: UserError) {
            e.printStackTrace()
        }
    }

    @Test
    fun `should update page`() = runTest {
        val edited = Page("Updated Page", "New Updates made")
        try {
            wikiInteractor.updatePage("1", edited)
            val page = wikiInteractor.fetchPage("1")

            page shouldBeEqualTo edited
        } catch (e: UserError) {
            fail("Should allow editing", e)
        }
    }

    @Test
    fun `should not create page if title or content not provided`() = runTest {
        try {
            wikiInteractor.createPage(Page("", "Content"))
            fail("Should not create if no title")
        } catch (e:Exception){}
        try {
            wikiInteractor.createPage(Page("Title", "   "))
            fail("Should not create if no content")
        } catch (e:Exception){}
    }

}