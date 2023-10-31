package com.vandenbreemen.ktt.presenter

import com.vandenbreemen.ktt.interactor.BreadcrumbsInteractor
import com.vandenbreemen.ktt.interactor.CustomCssInteractor
import com.vandenbreemen.ktt.interactor.WikiInteractor
import com.vandenbreemen.ktt.interactor.WikiPageTagsInteractor
import com.vandenbreemen.ktt.model.Page
import com.vandenbreemen.ktt.model.PageBreadcrumbItem
import com.vandenbreemen.ktt.model.PageSearchResult
import com.vandenbreemen.ktt.model.StylesheetType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WikiPresenter(private val wikiInteractor: WikiInteractor, private val pageTagsInteractor: WikiPageTagsInteractor,
        private val breadcrumbsInteractor: BreadcrumbsInteractor = BreadcrumbsInteractor(),
        private val customCssInteractor: CustomCssInteractor,
    ) {

    val breadcrumbTrail: List<PageBreadcrumbItem> get() = breadcrumbsInteractor.getBreadcrumbTrail()
    private val dispatcher = Dispatchers.IO

    val css: String get() = customCssInteractor.getCss()

    fun fetchPage(pageId: String): Page {
        return wikiInteractor.fetchPage(pageId)
    }

    fun updatePage(pageId: String, updated: Page) {
        CoroutineScope(dispatcher).launch {
            wikiInteractor.updatePage(pageId, updated)
        }
    }

    fun createPage(page: Page): Int {
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

    /**
     * Notify the system that we're viewing the given page
     */
    fun onViewPage(pageId: String, page: Page) {
        breadcrumbsInteractor.submitPage(pageId, page)
    }

    fun getCssByType(type: StylesheetType): String {
        return  customCssInteractor.getCssForType(type)
    }

    fun updateCssByType(type: StylesheetType, css: String) {
        CoroutineScope(dispatcher).launch {
            customCssInteractor.storeStylesheet(type, css)
        }
    }

    fun hasPreviousVersion(pageId: Int): Boolean {
        return wikiInteractor.fetchPreviousVersionOfPage(pageId.toString()) != null
    }

}