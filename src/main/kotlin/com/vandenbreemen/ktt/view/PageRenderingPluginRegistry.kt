package com.vandenbreemen.ktt.view

class PageRenderingPluginRegistry {

    private val registry: MutableList<PageRenderingPlugin> = mutableListOf()
    val plugins: Iterator<PageRenderingPlugin> get() = registry.iterator()

    fun register(plugin: PageRenderingPlugin) {
        this.registry.add(plugin)
    }

}