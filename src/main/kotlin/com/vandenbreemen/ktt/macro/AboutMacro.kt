package com.vandenbreemen.ktt.macro

class AboutMacro(): Macro {
    override val name: String
        get() = "kttAbout"

    override fun execute(args: Map<String, String>): String {

        val about = javaClass.getResource("/about.dat").readText()

        return """
```
$about
```
        """.trimIndent()
    }
}