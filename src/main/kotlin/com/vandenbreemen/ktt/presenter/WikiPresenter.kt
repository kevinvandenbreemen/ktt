package com.vandenbreemen.ktt.presenter

import com.vandenbreemen.ktt.interactor.WikiInteractor
import com.vandenbreemen.ktt.interactor.WikiPageTagsInteractor
import com.vandenbreemen.ktt.model.Page
import com.vandenbreemen.ktt.model.PageSearchResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WikiPresenter(private val wikiInteractor: WikiInteractor, private val pageTagsInteractor: WikiPageTagsInteractor) {

    private val dispatcher = Dispatchers.IO

    suspend fun fetchPage(pageId: String): Page {
        return wikiInteractor.fetchPage(pageId)
    }

    suspend fun updatePage(pageId: String, updated: Page) {
        CoroutineScope(dispatcher).launch {
            wikiInteractor.updatePage(pageId, updated)
        }
    }

    suspend fun createPage(page: Page): Int {
        return wikiInteractor.createPage(page)
    }

    fun searchPage(searchTerm: String): List<PageSearchResult> {
        val result = wikiInteractor.search(searchTerm)
        if(result.isEmpty()) {
            throw Error("No results found matching '$searchTerm'")
        }
        return result
    }

    fun handlePageTags(pageId: String, rawTagsString: String) {
        pageTagsInteractor.addUpdatePageTags(pageId, rawTagsString)
    }

    fun getTags(pageId: String): String {
        return pageTagsInteractor.getTags(pageId)
    }

}