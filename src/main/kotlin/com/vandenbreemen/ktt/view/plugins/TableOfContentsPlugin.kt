package com.vandenbreemen.ktt.view.plugins

import com.vandenbreemen.ktt.view.PageRenderingPlugin
import java.util.*

class TableOfContentsPlugin: PageRenderingPlugin {

    override fun process(markdownContent: String): String {
        val headersRegex = Regex("(?m)^#+\\s+(.*)$")

        val headers = headersRegex.findAll(markdownContent)
            .map {
                println("gr0=${it.groupValues[0]}")
                println("gr1=${it.groupValues[1]}")
                it.groupValues[0] to it.groupValues[1] }
            .toList()

        val headerLinks = mutableMapOf<String, String>()

        val tableOfContents = StringBuilder()

        if (headers.isNotEmpty()) {
            tableOfContents.append("Table of Contents\n")
            for ((hashes, header) in headers) {
                println("Processing '$header'")
                val indentation = "  ".repeat(hashes.count { it == '#' } - 1)
                tableOfContents.append("$indentation- [${header.trim()}](#${
                    header.lowercase(Locale.getDefault()).replace("\\s+".toRegex(), "-").also { link-> 
                    headerLinks[hashes] = link
                }})\n")
            }
        }

        println(headerLinks)
        var updatedMarkdown = markdownContent
        headerLinks.entries.forEach { headerToLink->
            println("Mapping:  $headerToLink")
            updatedMarkdown = updatedMarkdown.replace(headerToLink.key, "<section id=\"${headerToLink.value}\" />\n\n${headerToLink.key}\n")
        }

        return StringBuilder(tableOfContents.toString()).append("\n\n").append(updatedMarkdown).toString()
    }
}