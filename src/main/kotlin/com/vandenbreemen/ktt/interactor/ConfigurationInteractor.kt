package com.vandenbreemen.ktt.interactor

import com.vandenbreemen.ktt.persistence.SQLiteWikiRepository

/**
 * Handle anything relating to configuration of the system, including some UI shortcuts etc.
 */
class ConfigurationInteractor(private val wikiRepository: SQLiteWikiRepository) {

    fun getPort(): Int {
        return wikiRepository.getUIConfiguration().runPort
    }

}