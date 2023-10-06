package com.vandenbreemen.ktt.view

import com.vandenbreemen.ktt.interactor.MarkdownInteractor
import com.vandenbreemen.ktt.model.Page

class PageRenderingInteractor(private val markdownInteractor: MarkdownInteractor) {

    fun render(page: Page): String {
        return StringBuilder("<html><body>").apply {
            append("<h1>").append(page.title).append("</h1>")

            markdownInteractor.translateToHtml(page.content).let { html->
                append("<br/><br/>")
                append(html).append("</html>")
            }
        }.toString()
    }

}