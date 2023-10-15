package com.vandenbreemen.ktt.view.plugins

import com.vandenbreemen.ktt.macro.MacroRegistry
import com.vandenbreemen.ktt.view.PageRenderingPlugin

class MacrosPlugin(private val macroRegistry: MacroRegistry): PageRenderingPlugin {
    override fun process(markdown: String): String {
        val allMacros = "[{]@macro:([a-zA-Z0-9]+)[\\s]*([^}]*)[}]".toRegex().findAll(markdown)

        var resultantMarkdown = markdown

        println("MATCHES\n=======================")
        allMacros.forEach { macro->
            val macroExpression = macro.value
            val macroName = macro.groupValues[1]
            println("expression=$macroExpression, macroName=$macroName\nvalues=${macro.groupValues}")

            macroRegistry.getMacroByName(macroName) ?.let { macro ->

            } ?: run {
                resultantMarkdown = resultantMarkdown.replace(macroExpression, "\n\nUnknown Macro:  $macroName\n\n")
            }
        }

        return resultantMarkdown
    }
}