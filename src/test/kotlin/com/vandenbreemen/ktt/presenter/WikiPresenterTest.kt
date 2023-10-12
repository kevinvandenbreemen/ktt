package com.vandenbreemen.ktt.presenter

import com.vandenbreemen.ktt.interactor.TestWikiInteractor
import com.vandenbreemen.ktt.interactor.WikiInteractor
import com.vandenbreemen.ktt.interactor.WikiPageTagsInteractor
import com.vandenbreemen.ktt.model.Page
import com.vandenbreemen.ktt.model.PageBreadcrumbItem
import com.vandenbreemen.ktt.persistence.SQLiteWikiRepository
import org.amshove.kluent.shouldBeEqualTo
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
            ), WikiPageTagsInteractor(repository)
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

}