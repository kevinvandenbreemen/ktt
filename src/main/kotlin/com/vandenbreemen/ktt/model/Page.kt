package com.vandenbreemen.ktt.model

/**
 * Page contents.  Note that this is not a true database entity as it intentionally does not know about the ID of the underlying
 * page and is intended more as a pojo to be passed around to various views etc
 */
data class Page(val title: String,
    val content: String
    )