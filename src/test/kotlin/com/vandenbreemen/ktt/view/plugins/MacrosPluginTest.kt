package com.vandenbreemen.ktt.view.plugins

import com.vandenbreemen.ktt.api.SystemAccess
import com.vandenbreemen.ktt.interactor.SystemAccessInteractor
import com.vandenbreemen.ktt.macro.Macro
import com.vandenbreemen.ktt.macro.MacroRegistry
import com.vandenbreemen.ktt.persistence.SQLiteWikiRepository
import org.amshove.kluent.shouldContain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

internal class MacrosPluginTest {

    internal class HelloWorldTestMacro(): Macro {
        override val name: String
            get() = "HelloWorld"

        override val description: String?
            get() = "Unit testing macro"

        override fun execute(args: Map<String, String>, systemAccessInteractor: SystemAccess): String {
            val message = args["message"] ?: "(missing message)"
            val additional = args["additional"]
            if(additional != null) {
                return "got pair:  ${Pair<String, String>(message, additional).toString()}"
            }
            return "HiMsg:  $message"
        }
    }

    private val registry = MacroRegistry()
    val fileName = "macrosPluginTest.dat"
    private val plugin = MacrosPlugin(registry, SystemAccessInteractor(SQLiteWikiRepository(fileName)))

    @BeforeEach
    fun setup() {
        registry.register(HelloWorldTestMacro())
    }

    @AfterEach
    fun tearDown() {
        File(fileName).delete()
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

    @Test
    fun `should handle macros with multiple arguments`() {
        val markdown = """
# Hello world

this is a test of some wiki content.

{@macro:HelloWorld message=Hi how are you?, additional=this/is/a/test}

This is a special test.
        """.trimIndent()

        val rendered = plugin.process(markdown)

        println("RENDERED:\n===================\n$rendered")
        rendered.shouldContain("got pair:  (Hi how are you?, this/is/a/test)")
    }

}