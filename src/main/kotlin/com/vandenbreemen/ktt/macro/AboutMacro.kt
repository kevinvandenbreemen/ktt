package com.vandenbreemen.ktt.macro

import com.vandenbreemen.ktt.api.SystemAccess

internal class AboutMacro(): Macro {
    override val name: String
        get() = "kttAbout"

    override val description: String?
        get() = "Displays the standard about and ascii art for the wiki"

    override fun execute(args: Map<String, String>, systemAccess: SystemAccess): String {

        val about = javaClass.getResource("/about.dat").readText()

        return """
```
$about
```
        """.trimIndent()
    }
}