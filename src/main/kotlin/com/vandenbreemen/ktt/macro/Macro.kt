package com.vandenbreemen.ktt.macro

import com.vandenbreemen.ktt.interactor.SystemAccessInteractor

/**
 * Description of a macro along with logic for processing it etc
 */
interface Macro {

    val name: String

    /**
     * Description for more information on how the macro works etc
     */
    val description: String?

    /**
     * Execute with the parameters, returning content as a string.  Content should be in markdown
     */
    fun execute(args: Map<String, String>, systemAccessInteractor: SystemAccessInteractor): String
}