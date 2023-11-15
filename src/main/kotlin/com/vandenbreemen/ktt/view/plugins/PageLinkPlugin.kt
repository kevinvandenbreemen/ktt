package com.vandenbreemen.ktt.view.plugins

import com.vandenbreemen.ktt.persistence.SQLiteWikiRepository
import com.vandenbreemen.ktt.view.PageRenderingPlugin

class PageLinkPlugin(private val repository: SQLiteWikiRepository): PageRenderingPlugin {

    override fun process(markdown: String): String {
        return markdown.replace("(\\s|^)[\\[]([A-Za-z0-9 .()-]+)[\\]]".toRegex(setOf(RegexOption.MULTILINE))) {
            val linkText = it.groupValues[2]


            repository.searchPageByTitle(linkText)?.let { existingPageId->
                return@replace " [$linkText](/page/$existingPageId)"
            }

            " [$linkText](/page/create/$linkText)"
        }
    }
}