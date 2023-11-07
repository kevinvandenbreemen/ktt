package com.vandenbreemen.ktt.macro

import com.vandenbreemen.ktt.interactor.SystemAccessInteractor

class AboutMacro(): Macro {
    override val name: String
        get() = "kttAbout"

    override fun execute(args: Map<String, String>, systemAccessInteractor: SystemAccessInteractor): String {

        val about = javaClass.getResource("/about.dat").readText()

        return """
```
$about
```
        """.trimIndent()
    }
}