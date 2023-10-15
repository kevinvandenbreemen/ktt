package com.vandenbreemen.ktt.macro

class AboutMacro(): Macro {
    override val name: String
        get() = "kttAbout"

    override fun execute(args: Map<String, String>): String {
        return """
KTT (KTor Training) Wiki Software

Software written by Kevin VanDenBreemen as a fun project.  I hope this wiki can help you too
        """.trimIndent()
    }
}