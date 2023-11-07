package com.vandenbreemen.ktt.macro

import com.vandenbreemen.ktt.interactor.SystemAccessInteractor

class MacroRegistryMacro(private val macroRegistry: MacroRegistry): Macro {

    override val name: String
        get() = "macros"

    override val description: String?
        get() = "Lists all macros currently available in this wiki"

    override fun execute(args: Map<String, String>, systemAccessInteractor: SystemAccessInteractor): String {

        return """
---------------------------------
## Available Macros
${StringBuilder().apply { 
    macroRegistry.allMacros.forEach { macro->
        append("### ").append(macro.name).append("\n").append(macro.description).append("\n\n")
    }
        }}
--------------------------------
        """.trimIndent()

    }
}