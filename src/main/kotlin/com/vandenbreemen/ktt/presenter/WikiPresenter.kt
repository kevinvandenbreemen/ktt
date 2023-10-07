package com.vandenbreemen.ktt.presenter

import com.vandenbreemen.ktt.interactor.WikiInteractor
import com.vandenbreemen.ktt.model.Page
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WikiPresenter(private val wikiInteractor: WikiInteractor) {

    private val dispatcher = Dispatchers.IO

    suspend fun fetchPage(pageId: String): Page {
        return wikiInteractor.fetchPage(pageId)
    }

    suspend fun updatePage(pageId: String, updated: Page) {
        CoroutineScope(dispatcher).launch {
            wikiInteractor.updatePage(pageId, updated)
        }
    }

}