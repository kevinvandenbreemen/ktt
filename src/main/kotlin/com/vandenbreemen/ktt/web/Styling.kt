package com.vandenbreemen.ktt.web

object Classes {
    const val breadcrumbs = "breadcrumb_bar"
    const val editor = "editor_section"
    const val controlPanel = "control_panel"
    const val topSection = "top_section"
    const val wikiEntry = "wiki_entry"
    const val resultsSection = "result_section"
    const val errorSection = "error_section"
    const val item = "item"
}

object Colors {
    const val background = "C9D1FF"
    const val section = "E8EBFF"
    const val error = "DBC755"
    const val errorText = "AC7FFF"
}

const val css =
"""

.editor_section {
    
}

.editor_section input {
        width: 100%;
    }
    
.editor_section textarea {
    width: 100%;
    height: 80%;
    margin-top: 1em;
}

.${Classes.resultsSection} a {
    background-color: ${Colors.section}
}

.item a {
    
}

.${Classes.errorSection} {
    background-color: ${Colors.error}
}
"""