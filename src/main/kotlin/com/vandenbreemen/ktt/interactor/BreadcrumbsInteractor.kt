package com.vandenbreemen.ktt.interactor

import com.vandenbreemen.ktt.model.Page
import com.vandenbreemen.ktt.model.PageBreadcrumbItem

/**
 * Logic pertaining to breadcrumb trail as the user navigates around
 */
class BreadcrumbsInteractor {

    private val breadcrumbTrail = mutableListOf<PageBreadcrumbItem>()

    fun submitPage(pageId: String, page: Page) {
        breadcrumbTrail.lastOrNull()?.let { lastItem->
            if (lastItem.pageId == pageId) {
                return
            }
        }

        //  Don't allow loops of breadcrumb items
        breadcrumbTrail.indexOfFirst { existingItem->existingItem.pageId == pageId }.let { index->
            if(index >= 0) {
                val newList = breadcrumbTrail.slice(0 .. index)
                breadcrumbTrail.clear()
                breadcrumbTrail.addAll(newList)
                return
            }
        }

        breadcrumbTrail.add(PageBreadcrumbItem(pageId, page.title))
    }

    fun getBreadcrumbTrail(): List<PageBreadcrumbItem> {
        return breadcrumbTrail.toList()
    }

}