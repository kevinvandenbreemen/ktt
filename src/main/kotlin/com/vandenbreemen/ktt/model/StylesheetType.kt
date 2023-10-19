package com.vandenbreemen.ktt.model

import com.vandenbreemen.ktt.web.Classes.breadcrumbs
import com.vandenbreemen.ktt.web.Classes.controlPanel
import com.vandenbreemen.ktt.web.Classes.wikiEntry

enum class StylesheetType(val cssClass: String) {
    WikiEntry(wikiEntry),
    ControlPanel(controlPanel),
    BreadcrumbsSection(breadcrumbs),
}
