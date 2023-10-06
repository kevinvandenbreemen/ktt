package com.vandenbreemen.ktt.interactor

import com.vandenbreemen.ktt.model.Page
import java.lang.RuntimeException

class WikiInteractor(private val testWikiInteractor: TestWikiInteractor) {

    /**
     * fetch the page with the given ID
     */
    suspend fun fetchPage(id: String): Page? {
        if(testWikiInteractor.isTestID(id)) {
            return testWikiInteractor.getTestWiki()
        }

        TODO("Still need to implement the regular functionality")
    }

}