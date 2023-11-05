package com.vandenbreemen.ktt.main

import com.vandenbreemen.ktt.interactor.StaticContentInteractor
import com.vandenbreemen.ktt.macro.AboutMacro
import com.vandenbreemen.ktt.macro.MacroRegistry
import com.vandenbreemen.ktt.persistence.SQLiteWikiRepository
import com.vandenbreemen.ktt.view.PageRenderingPluginRegistry
import com.vandenbreemen.ktt.view.plugins.MacrosPlugin
import com.vandenbreemen.ktt.view.plugins.PageLinkPlugin
import com.vandenbreemen.ktt.view.plugins.TableOfContentsPlugin
import com.vandenbreemen.ktt.web.startServer

/**
 * Singleton that is reponsible for managing everything in the app etc
 */
object WikiApplication {

    private val staticContentInteractor = StaticContentInteractor()
    private val repository = SQLiteWikiRepository(("main.db"))

    val pageRenderingPluginRegistry: PageRenderingPluginRegistry by lazy {
        PageRenderingPluginRegistry()
    }

    val macroRegistry: MacroRegistry by lazy {
        MacroRegistry().also {
            pageRenderingPluginRegistry.register(MacrosPlugin(it))
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {

        macroRegistry.register(AboutMacro())

        pageRenderingPluginRegistry.register(PageLinkPlugin(repository))
        pageRenderingPluginRegistry.register(TableOfContentsPlugin())

        startServer(repository, staticContentInteractor)
    }

}