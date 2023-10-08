package com.vandenbreemen.ktt.view.plugins

import com.vandenbreemen.ktt.view.PageRenderingPlugin

class PageLinkPlugin(): PageRenderingPlugin {

    override fun process(markdown: String): String {
        return markdown.replace("([\\[]([^]]+)[\\]])".toRegex(setOf(RegexOption.MULTILINE))) {
            val linkText = it.groupValues[2]
            "[$linkText](/page/create/$linkText)"
        }
    }
}