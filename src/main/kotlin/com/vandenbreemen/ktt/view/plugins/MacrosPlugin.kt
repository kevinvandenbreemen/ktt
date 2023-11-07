package com.vandenbreemen.ktt.view.plugins

import com.vandenbreemen.ktt.interactor.SystemAccessInteractor
import com.vandenbreemen.ktt.macro.MacroRegistry
import com.vandenbreemen.ktt.view.PageRenderingPlugin

class MacrosPlugin(private val macroRegistry: MacroRegistry, private val systemAccessInteractor: SystemAccessInteractor): PageRenderingPlugin {
    override fun process(markdown: String): String {
        val allMacros = "[{]@macro:([a-zA-Z0-9]+)[\\s]*([^}]*)[}]".toRegex().findAll(markdown)

        var resultantMarkdown = markdown

        allMacros.forEach { macro->
            val macroExpression = macro.value
            val macroName = macro.groupValues[1]

            val argMap: MutableMap<String, String> = mutableMapOf()
            if(macro.groupValues[2].isNotEmpty()) {
                val argumentsRaw = macro.groupValues[2].split("[\\s]*[,][\\s]*".toRegex())
                argumentsRaw.mapNotNull { argument ->
                    argument.split("=").let { rawPair ->
                        if(rawPair.size != 2) {
                            return@mapNotNull null
                        }
                        Pair(rawPair[0], rawPair[1])
                    }
                }.toList().forEach { pair->
                    argMap[pair.first] = pair.second
                }
            }

            macroRegistry.getMacroByName(macroName) ?.let { macro ->
                resultantMarkdown = resultantMarkdown.replace(macroExpression, macro.execute(argMap, systemAccessInteractor))
            } ?: run {
                resultantMarkdown = resultantMarkdown.replace(macroExpression, "\n\nUnknown Macro:  $macroName\n\n")
            }
        }

        return resultantMarkdown
    }
}