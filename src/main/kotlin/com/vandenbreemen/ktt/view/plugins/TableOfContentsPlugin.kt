package com.vandenbreemen.ktt.view.plugins

import com.vandenbreemen.ktt.view.PageRenderingPlugin
import java.util.*

class TableOfContentsPlugin: PageRenderingPlugin {

    override fun process(markdownContent: String): String {
        val headersRegex = Regex("(?m)^#+\\s+(.*)$")

        val headers = headersRegex.findAll(markdownContent)
            .map {
                it.groupValues[0] to it.groupValues[1] }
            .toList()

        val headerLinks = mutableMapOf<String, String>()

        val tableOfContents = StringBuilder()

        if (headers.isNotEmpty()) {
            tableOfContents.append("Table of Contents\n")
            for ((hashes, header) in headers) {
                val indentation = "  ".repeat(hashes.count { it == '#' } - 1)
                tableOfContents.append("$indentation- [${header.trim()}](#${
                    header.lowercase(Locale.getDefault()).replace("\\s+".toRegex(), "-").also { link-> 
                    headerLinks[hashes] = link
                }})\n")
            }
        }

        var updatedMarkdown = markdownContent
        headerLinks.entries.forEach { headerToLink->
            updatedMarkdown = updatedMarkdown.replace(headerToLink.key, "<section id=\"${headerToLink.value}\">\n\n${headerToLink.key}\n</section>")
        }

        return StringBuilder(tableOfContents.toString()).append("\n\n").append(updatedMarkdown).toString()
    }
}