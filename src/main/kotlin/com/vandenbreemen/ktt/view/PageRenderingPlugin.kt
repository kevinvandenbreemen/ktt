package com.vandenbreemen.ktt.view

interface PageRenderingPlugin {

    /**
     * Processes through the markdown, returning updated markdown as a result
     */
    fun process(markdown: String): String

}