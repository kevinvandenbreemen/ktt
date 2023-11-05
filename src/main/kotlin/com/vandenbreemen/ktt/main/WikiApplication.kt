package com.vandenbreemen.ktt.main

import com.vandenbreemen.ktt.interactor.*
import com.vandenbreemen.ktt.macro.AboutMacro
import com.vandenbreemen.ktt.macro.MacroRegistry
import com.vandenbreemen.ktt.persistence.SQLiteWikiRepository
import com.vandenbreemen.ktt.presenter.WikiPresenter
import com.vandenbreemen.ktt.view.PageRenderingInteractor
import com.vandenbreemen.ktt.view.PageRenderingPluginRegistry
import com.vandenbreemen.ktt.view.plugins.MacrosPlugin
import com.vandenbreemen.ktt.view.plugins.PageLinkPlugin
import com.vandenbreemen.ktt.view.plugins.TableOfContentsPlugin
import com.vandenbreemen.ktt.web.startServer

/**
 * Singleton that is reponsible for managing everything in the app etc
 */
object WikiApplication {

    private val pageRenderingPluginRegistry: PageRenderingPluginRegistry by lazy {
        PageRenderingPluginRegistry()
    }

    val macroRegistry: MacroRegistry by lazy {
        MacroRegistry().also {
            pageRenderingPluginRegistry.register(MacrosPlugin(it))
        }
    }

    private val staticContentInteractor = StaticContentInteractor()
    private val repository = SQLiteWikiRepository(("main.db"))

    private val renderingInteractor = PageRenderingInteractor(MarkdownInteractor(), pageRenderingPluginRegistry)
    private val configInteractor = ConfigurationInteractor(repository)

    private val presenter = WikiPresenter( WikiInteractor(TestWikiInteractor(), repository), WikiPageTagsInteractor(repository),
        customCssInteractor = CustomCssInteractor(repository)
    )

    @JvmStatic
    fun main(args: Array<String>) {

        val about = javaClass.getResource("/about.dat").readText()
        println(about)

        macroRegistry.register(AboutMacro())

        pageRenderingPluginRegistry.register(PageLinkPlugin(repository))
        pageRenderingPluginRegistry.register(TableOfContentsPlugin())

        startServer(staticContentInteractor, configInteractor, renderingInteractor, presenter)
    }

}