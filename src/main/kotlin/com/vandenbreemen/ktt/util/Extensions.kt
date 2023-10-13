package com.vandenbreemen.ktt.util

//  Code borrowed from a similar issue faced by jetbrains' markdown plugin
//  see https://github.com/JetBrains/gradle-changelog-plugin/pull/177/commits/9bb6cc964fec4ee544bed16c1ba32e497b1641ab#diff-6101b1b5f01f6e9a4a63881c247a3293eb79f025e5bdef60aef845968d5a4e38
internal fun String.normalizeToLineFeed(): String {
    val result = listOf(
        "\r\n" to "\n",
        "\r" to "\n",
    ).fold(this) { acc, (pattern, replacement) ->
        acc.replace(pattern, replacement)
    }

    return result
}