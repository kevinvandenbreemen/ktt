package com.vandenbreemen.ktt.view.plugins

import com.vandenbreemen.ktt.macro.Macro
import com.vandenbreemen.ktt.macro.MacroRegistry
import org.amshove.kluent.shouldContain
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MacrosPluginTest {

    class HelloWorldTestMacro(): Macro {
        override val name: String
            get() = "HelloWorld"

        override fun execute(args: Map<String, String>): String {
            val message = args["message"] ?: "(missing message)"
            return "HiMsg:  $message"
        }
    }

    private val registry = MacroRegistry()
    private val plugin = MacrosPlugin(registry)

    @BeforeEach
    fun setup() {
        registry.register(HelloWorldTestMacro())
    }

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

    @Test
    fun `should execute a macro`() {
        val markdown = """
# Hello world

this is a test of some wiki content.

{@macro:HelloWorld message=Hi how are you?}

This is a special test.
        """.trimIndent()

        val rendered = plugin.process(markdown)

        println("RENDERED:\n===================\n$rendered")
        rendered.shouldContain("HiMsg:  Hi how are you?")
    }

    @Test
    fun `should gracefully handle badly formatted macro`() {
        val markdown = """
# Hello world

this is a test of some wiki content.

{@macro:HelloWorld Hi how are you?}

This is a special test.
        """.trimIndent()

        val rendered = plugin.process(markdown)

        println("RENDERED:\n===================\n$rendered")
        rendered.shouldContain("HiMsg:  (missing message)")
    }

}