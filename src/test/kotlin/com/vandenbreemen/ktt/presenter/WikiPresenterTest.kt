package com.vandenbreemen.ktt.presenter

import com.vandenbreemen.ktt.interactor.CustomCssInteractor
import com.vandenbreemen.ktt.interactor.TestWikiInteractor
import com.vandenbreemen.ktt.interactor.WikiInteractor
import com.vandenbreemen.ktt.interactor.WikiPageTagsInteractor
import com.vandenbreemen.ktt.model.Page
import com.vandenbreemen.ktt.model.PageBreadcrumbItem
import com.vandenbreemen.ktt.persistence.SQLiteWikiRepository
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

class WikiPresenterTest {

    private lateinit var wikiPresenter: WikiPresenter
    private val filePage = "wikiPres.dat"

    @BeforeEach
    fun setup() {
        val repository = SQLiteWikiRepository(filePage)
        wikiPresenter = WikiPresenter(WikiInteractor(
            TestWikiInteractor(),
            repository
            ), WikiPageTagsInteractor(repository),
            customCssInteractor = CustomCssInteractor(repository)
        )
    }

    @AfterEach
    fun tearDown() {
        File(filePage).delete()
    }

    @Test
    fun `should generate breadcrumb trail based on previously viewed pages`() {
        wikiPresenter.onViewPage("1", Page("page 1", "Content 1"))
        wikiPresenter.onViewPage("2", Page("page 2", "Content 2"))

        wikiPresenter.breadcrumbTrail.let {
            it.size shouldBeEqualTo 2
            it[0] shouldBeEqualTo PageBreadcrumbItem("1", "page 1")
            it[1] shouldBeEqualTo PageBreadcrumbItem("2", "page 2")
        }

    }

    @Test
    fun `should not allow duplicate entries in the breadcrumb trail`() {
        wikiPresenter.onViewPage("1", Page("page 1", "Content 1"))
        wikiPresenter.onViewPage("2", Page("page 2", "Content 2"))
        wikiPresenter.onViewPage("2", Page("page 2", "Content 2"))

        wikiPresenter.breadcrumbTrail.let {
            it.size shouldBeEqualTo 2
            it[0] shouldBeEqualTo PageBreadcrumbItem("1", "page 1")
            it[1] shouldBeEqualTo PageBreadcrumbItem("2", "page 2")
        }
    }

    @Test
    fun `should prevent loops of visited pages`() {
        wikiPresenter.onViewPage("1", Page("page 1", "Content 1"))
        wikiPresenter.onViewPage("2", Page("page 2", "Content 2"))
        wikiPresenter.onViewPage("3", Page("page 3", "Content 2"))

        wikiPresenter.breadcrumbTrail.let {
            it.size shouldBeEqualTo 3
            it[0] shouldBeEqualTo PageBreadcrumbItem("1", "page 1")
            it[1] shouldBeEqualTo PageBreadcrumbItem("2", "page 2")
            it[2] shouldBeEqualTo PageBreadcrumbItem("3", "page 3")
        }

        wikiPresenter.onViewPage("2", Page("page 2", "Content 2"))

        wikiPresenter.breadcrumbTrail.let {
            it.size shouldBeEqualTo 2
            it[0] shouldBeEqualTo PageBreadcrumbItem("1", "page 1")
            it[1] shouldBeEqualTo PageBreadcrumbItem("2", "page 2")
        }
    }

    @Test
    fun `should provide for checking if there is a previous version of a page`() = runTest{
        val id = wikiPresenter.createPage(Page("page1", "test page"))
        wikiPresenter.updatePage(id.toString(), Page("page1", "Updated page"))
        Thread.sleep(1000)  //  Cheesy but updating pages is done as a spawned coroutine
        wikiPresenter.hasPreviousVersion(id).shouldBeTrue()
    }

    @Test
    fun `should not find previous version of initially created page`() {
        val id = wikiPresenter.createPage(Page("page1", "test page"))
        wikiPresenter.hasPreviousVersion(id).shouldBeFalse()
    }

}