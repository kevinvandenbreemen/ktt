package com.vandenbreemen.ktt.interactor

import com.vandenbreemen.ktt.message.UserError
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

    fun updatePage(id: String, updatedPage: Page) {
        if(updatedPage.title.isBlank()) {
            throw UserError("Please provide a title")
        } else if (updatedPage.content.isBlank()) {
            throw UserError("Please provide content for your page")
        }
        repository.searchPageByTitle(updatedPage.title)?.let { existingId->
            if(existingId != id.toInt()) {
                throw UserError("Cannot update the title to '${updatedPage.title}' because another page already exists with that title")
            }
        }
        repository.updatePage(id, updatedPage)
    }

    suspend fun createPage(page: Page): Int {
        if(page.title.isBlank()) {
            throw UserError("Please provide a title")
        } else if (page.content.isBlank()) {
            throw UserError("Please provide content for your page")
        }
        repository.searchPageByTitle(page.title)?.let { _->
            throw UserError("There is another page with title '${page.title}' already in the system")
        }
        return repository.createPage(page)
    }

}