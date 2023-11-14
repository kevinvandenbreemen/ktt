package com.vandenbreemen.ktt.view.plugins

import com.vandenbreemen.ktt.persistence.SQLiteWikiRepository
import com.vandenbreemen.ktt.view.PageRenderingPlugin

class PageLinkPlugin(private val repository: SQLiteWikiRepository): PageRenderingPlugin {

    override fun process(markdown: String): String {
        return markdown.replace("([^!])([\\[]([A-Za-z0-9 .()-]+)[\\]])".toRegex(setOf(RegexOption.MULTILINE))) {
            val linkText = it.groupValues[3]
            val prefixChar = it.groupValues[1]

            repository.searchPageByTitle(linkText)?.let { existingPageId->
                return@replace "$prefixChar[$linkText](/page/$existingPageId)"
            }

            "$prefixChar[$linkText](/page/create/$linkText)"
        }
    }
}