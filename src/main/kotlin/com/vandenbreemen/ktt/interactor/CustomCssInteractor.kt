package com.vandenbreemen.ktt.interactor

import com.vandenbreemen.ktt.model.StylesheetType
import com.vandenbreemen.ktt.persistence.SQLiteWikiRepository
import com.vandenbreemen.ktt.web.css

class CustomCssInteractor(private val repository: SQLiteWikiRepository) {

    /**
     * Creates/updates stylesheet for the given type of content
     */
    fun storeStylesheet(type: StylesheetType, css: String) {
        repository.storeUpdateCss(type, css)
    }

    /**
     * Gets the CSS for display on the web
     */
    fun getCss(): String {
        val storedCss = repository.getCss()
        return StringBuilder(css).append("\n").append(storedCss).toString()
    }

}