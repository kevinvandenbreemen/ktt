package com.vandenbreemen.ktt.persistence

import com.vandenbreemen.ktt.message.NoSuchPageError
import com.vandenbreemen.ktt.model.Page
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.fail
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.io.File

class SQLiteWikiRepositoryTest() {

    companion object {
        const val filename = "test.db"

    }

    @AfterEach
    fun setup() {
        File(filename).delete()
    }


    @Test
    fun `should initialize the database`() {
        SQLiteWikiRepository(filename)
    }

    @Test
    fun `should store a page`() = runTest{
        val repo = SQLiteWikiRepository(filename)
        val page = Page("First Stored Page", "This is a test of storing a page")
        repo.createPage(page)
        repo.loadPage("1").shouldBeEqualTo(page)
    }

    @Test
    fun `should provide ID of last created page when creating a page`() = runTest{
        val repo = SQLiteWikiRepository(filename)
        val page = Page("First Stored Page", "This is a test of storing a page")
        repo.createPage(page)
        val id = repo.createPage(Page("another page", "its content"))
        id shouldBeEqualTo 2
    }

    @Test
    fun `should handle no such page`() = runTest {
        val repo = SQLiteWikiRepository(filename)
        val page = Page("First Stored Page", "This is a test of storing a page")
        repo.createPage(page)

        try {
            val retrieved = repo.loadPage("2")
            fail("Missing page should have raised an error")
        } catch (e: NoSuchPageError) {
            e.printStackTrace()
        }
    }

    @Test
    fun `should handle editing an existing page`()  = runTest{
        val repo = SQLiteWikiRepository(filename)
        val page = Page("First Stored Page", "This is a test of storing a page")
        repo.createPage(page)
        repo.loadPage("1")

        val updated = Page("Updated First Page", "This is some updates to the first page")
        repo.updatePage("1", updated)

        repo.loadPage("1").run {
            title shouldBeEqualTo "Updated First Page"
            content shouldBeEqualTo "This is some updates to the first page"
        }
    }

    @Test
    fun `should prevent creating 2 pages with the same title`() {
        val repo = SQLiteWikiRepository(filename)
        val page = Page("First Stored Page", "This is a test of storing a page")
        repo.createPage(page)

        try {
            repo.createPage(Page("First Stored Page", "some other page but with the same title"))
            fail("Shouldn't allow creating pages with same title")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Test
    fun `should search pages by like`() {
        val repo = SQLiteWikiRepository(filename)
        repo.createPage(Page("First Stored Page", "This is a test of storing a page"))
        repo.createPage(Page("Second Page", "the grass is full of hummingburds"))
        repo.createPage(Page("Third Page", "there's a vast web of internets"))

        val result = repo.searchPages("as")
        result.size shouldBeEqualTo 2

    }

}