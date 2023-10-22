package com.vandenbreemen.ktt.interactor

import com.vandenbreemen.ktt.persistence.SQLiteWikiRepository
import com.vandenbreemen.ktt.view.UIConfiguration

/**
 * Handle anything relating to configuration of the system, including some UI shortcuts etc.
 */
class ConfigurationInteractor(private val wikiRepository: SQLiteWikiRepository) {

    fun getPort(): Int {
        return wikiRepository.getUIConfiguration().runPort
    }

    fun getUIConfiguration(): UIConfiguration {
        return wikiRepository.getUIConfiguration()
    }

    fun updateUIConfiguration(configuration: UIConfiguration) {
        this.wikiRepository.updateUIConfiguration(configuration)
    }

}