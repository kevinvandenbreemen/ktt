package com.vandenbreemen.ktt.interactor

import com.vandenbreemen.ktt.model.Page
import com.vandenbreemen.ktt.model.PageBreadcrumbItem

/**
 * Logic pertaining to breadcrumb trail as the user navigates around
 */
class BreadcrumbsInteractor {

    private val breadcrumbTrail = mutableListOf<PageBreadcrumbItem>()

    fun submitPage(pageId: String, page: Page) {
        breadcrumbTrail.add(PageBreadcrumbItem(pageId, page.title))
    }

    fun getBreadcrumbTrail(): List<PageBreadcrumbItem> {
        return breadcrumbTrail.toList()
    }

}