package com.vandenbreemen.ktt.interactor

import java.io.File

/**
 * For serving up static content from the wiki
 */
class StaticContentInteractor {

    /**
     * Directory name.  Will be relative to the running instance
     */
    val staticContentRoot = "kttStatic"
    val staticContentWebPath = "/res"

    /**
     * Set up the static content directory
     */
    fun setupDirectory() {
        File(staticContentRoot).mkdir()
    }



}