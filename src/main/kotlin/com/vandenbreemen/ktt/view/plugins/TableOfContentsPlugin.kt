package com.vandenbreemen.ktt.view.plugins

import com.vandenbreemen.kmarkdown.preparser.TOCPreparser
import com.vandenbreemen.ktt.view.PageRenderingPlugin

class TableOfContentsPlugin: PageRenderingPlugin {

    override fun process(markdownContent: String): String {
        return TOCPreparser().parse(markdownContent, "Table of Contents")
    }
}