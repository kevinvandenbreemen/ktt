package com.vandenbreemen.ktt.interactor

import com.vandenbreemen.ktt.persistence.SQLiteWikiRepository

class WikiPageTagsInteractor(private val repository: SQLiteWikiRepository) {

    fun addUpdatePageTags(pageId: String, tagsString: String) {
        val tags = tagsString.split("[\\s]*[,][\\s]*".toRegex())
        repository.removeTagsForPage(pageId)
        tags.forEach { tag->
            repository.assignPageTag(pageId, tag)
        }
    }

    /**
     * Gets the tags of the page with the given ID as a comma-separated string
     */
    fun getTags(pageId: String): String {
        return repository.getPageTags(pageId).joinToString(", ")
    }

}