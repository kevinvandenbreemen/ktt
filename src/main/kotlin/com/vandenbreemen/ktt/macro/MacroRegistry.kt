package com.vandenbreemen.ktt.macro

/**
 * All the macros registered in the system
 */
class MacroRegistry {

    private val macrosByName = mutableMapOf<String, Macro>()

    fun register(macro: Macro) {
        macrosByName[macro.name] = macro
    }

    fun getMacroByName(name: String): Macro? {
        return macrosByName[name]
    }

}