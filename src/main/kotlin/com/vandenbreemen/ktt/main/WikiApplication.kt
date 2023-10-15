package com.vandenbreemen.ktt.main

import com.vandenbreemen.ktt.view.PageRenderingPluginRegistry
import com.vandenbreemen.ktt.web.startServer

/**
 * Singleton that is reponsible for managing everything in the app etc
 */
object WikiApplication {

    val pageRenderingPluginRegistry: PageRenderingPluginRegistry by lazy {
        PageRenderingPluginRegistry()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        startServer()
    }

}