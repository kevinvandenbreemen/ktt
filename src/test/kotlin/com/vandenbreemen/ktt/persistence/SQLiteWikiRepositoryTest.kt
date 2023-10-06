package com.vandenbreemen.ktt.persistence

import com.vandenbreemen.ktt.message.NoSuchPageError
import com.vandenbreemen.ktt.model.Page
import kotlinx.coroutines.future.await
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
        val retrieved = repo.loadPage("1")

        retrieved.await().shouldBeEqualTo(page)
    }

    @Test
    fun `should handle no such page`() = runTest {
        val repo = SQLiteWikiRepository(filename)
        val page = Page("First Stored Page", "This is a test of storing a page")
        repo.createPage(page)
        val retrieved = repo.loadPage("2")

        try {
            retrieved.await()
            fail("Missing page should have raised an error")
        } catch (e: NoSuchPageError) {
            e.printStackTrace()
        }
    }

}