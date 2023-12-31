package com.vandenbreemen.ktt.interactor

import com.vandenbreemen.ktt.model.Page
import com.vandenbreemen.ktt.persistence.SQLiteWikiRepository
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.io.File

internal class WikiPageTagsInteractorTest {
    val testWikiInteractor = TestWikiInteractor()
    val filePath = "interactor_test.db"
    val repository = SQLiteWikiRepository(filePath)
    val wikiInteractor = WikiInteractor(testWikiInteractor, repository)
    val wikiPageTagsInteractor = WikiPageTagsInteractor(repository)

    @AfterEach
    fun tearDown() {
        File(filePath).delete()
    }

    @Test
    fun `should assign tags to a page`()  {
        val pageId = wikiInteractor.createPage(Page("Test", "Test creating tags"))
        wikiPageTagsInteractor.addUpdatePageTags(pageId.toString(), "tag1, tag2, tag3")
        val tags = wikiPageTagsInteractor.getTags(pageId.toString())
        tags shouldBeEqualTo "tag1, tag2, tag3"
    }

    @Test
    fun `should handle writing same tag twice to a page`()  {
        val pageId = wikiInteractor.createPage(Page("Test", "Test creating tags"))
        wikiPageTagsInteractor.addUpdatePageTags(pageId.toString(), "tag1, tag2, tag3")
        wikiPageTagsInteractor.addUpdatePageTags(pageId.toString(), "tag1, tag2, tag3")
        val tags = wikiPageTagsInteractor.getTags(pageId.toString())
        tags shouldBeEqualTo "tag1, tag2, tag3"
    }

    @Test
    fun `should handle removing tags from a page`() {
        val pageId = wikiInteractor.createPage(Page("Test", "Test creating tags"))
        wikiPageTagsInteractor.addUpdatePageTags(pageId.toString(), "tag1, tag2, tag3")
        wikiPageTagsInteractor.addUpdatePageTags(pageId.toString(), "tag1, tag2")
        val tags = wikiPageTagsInteractor.getTags(pageId.toString())
        tags shouldBeEqualTo "tag1, tag2"
    }
}