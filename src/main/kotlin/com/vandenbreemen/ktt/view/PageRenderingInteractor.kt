package com.vandenbreemen.ktt.view

import com.vandenbreemen.ktt.interactor.MarkdownInteractor
import com.vandenbreemen.ktt.model.Page

class PageRenderingInteractor(private val markdownInteractor: MarkdownInteractor, private val pluginRegistry: PageRenderingPluginRegistry) {

    fun render(page: Page): String {
        return StringBuilder("<html><body>").apply {
            append("<h1>").append(page.title).append("</h1>")

            //  Handle all plugins
            var currentText = page.content
            pluginRegistry.plugins.forEach { plugin->
                currentText = plugin.process(currentText)
            }

            markdownInteractor.translateToHtml(currentText).let { html->
                append("<br/><br/>")
                append(html).append("</html>")
            }
        }.toString()
    }

}