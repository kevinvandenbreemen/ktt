package com.vandenbreemen.ktt.web

object Classes {
    const val editor = "editor_section"
    const val controlPanel = "control_panel"
    const val topSection = "top_section"
    const val wikiEntry = "wiki_entry"
}

const val css =
"""
.control_panel {
    background-color: red;
}

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
"""