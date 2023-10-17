package com.vandenbreemen.ktt.interactor

import com.vandenbreemen.ktt.util.normalizeToLineFeed
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser

class MarkdownInteractor {

    fun translateToHtml(markdown: String): String {

        val toTranslate = markdown.normalizeToLineFeed()

        val flavour = GFMFlavourDescriptor()
        val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(toTranslate)
        return HtmlGenerator(toTranslate, parsedTree, flavour).generateHtml()
    }

}