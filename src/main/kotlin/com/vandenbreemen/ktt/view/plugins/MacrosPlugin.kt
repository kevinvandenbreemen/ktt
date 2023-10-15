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
            val argumentsRaw = macro.groupValues[2].split("[\\s]*[,][\\s]*")
            println("args=$argumentsRaw")


            val argMap: MutableMap<String, String> = mutableMapOf()
            if(argumentsRaw.isNotEmpty()) {
                val argPairs: List<Pair<String, String>> = argumentsRaw.mapNotNull { argument ->
                    argument.split("=").let { rawPair ->
                        println("rawPair=${rawPair}")
                        if(rawPair.size != 2) {
                            return@mapNotNull null
                        }
                        Pair(rawPair[0], rawPair[1])
                    }
                }.toList()
                argPairs.forEach { pair ->
                    argMap[pair.first] = pair.second
                }
            }

            println("expression=$macroExpression, macroName=$macroName\nvalues=${macro.groupValues}")

            macroRegistry.getMacroByName(macroName) ?.let { macro ->
                resultantMarkdown = resultantMarkdown.replace(macroExpression, macro.execute(argMap))
            } ?: run {
                resultantMarkdown = resultantMarkdown.replace(macroExpression, "\n\nUnknown Macro:  $macroName\n\n")
            }
        }

        return resultantMarkdown
    }
}