package com.vandenbreemen.ktt.interactor

import com.vandenbreemen.ktt.model.Page
import com.vandenbreemen.ktt.persistence.SQLiteWikiRepository

class WikiInteractor(private val testWikiInteractor: TestWikiInteractor, private val repository: SQLiteWikiRepository) {

    /**
     * fetch the page with the given ID
     */
    suspend fun fetchPage(id: String): Page {
        if(testWikiInteractor.isTestID(id)) {
            return testWikiInteractor.getTestWiki()
        }

        return repository.loadPage(id)
    }

    suspend fun updatePage(id: String, updatedPage: Page) {
        //  TODO    Validation of page content
        repository.updatePage(id, updatedPage)
    }

}