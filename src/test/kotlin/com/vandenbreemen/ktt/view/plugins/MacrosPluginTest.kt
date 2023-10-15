package com.vandenbreemen.ktt.view.plugins

import com.vandenbreemen.ktt.macro.MacroRegistry
import org.amshove.kluent.shouldContain
import org.junit.jupiter.api.Test

class MacrosPluginTest {

    private val registry = MacroRegistry()
    private val plugin = MacrosPlugin(registry)

    @Test
    fun `should render unknown macro if no macro with given name exists`() {
        val markdown = """
# Hello world

this is a test of some wiki content.

{@macro:FakeMacro}

This is a special test.
        """.trimIndent()

        val rendered = plugin.process(markdown)

        println("RENDERED:\n===================\n$rendered")
        rendered.shouldContain("Unknown Macro:  FakeMacro")
    }

}