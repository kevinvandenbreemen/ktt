package com.vandenbreemen.ktt.interactor

import com.vandenbreemen.ktt.persistence.SQLiteWikiRepository

/**
 * Interactor that provides access to a limited set of features on the KTT system.  Use this
 * for allowing plugins/macros to store and retrieve their own types of data
 */
class SystemAccessInteractor(private val repository: SQLiteWikiRepository) {
    fun storeValue(key: String, value: String) {
        repository.storeValue(key, value)
    }

    fun retrieveValue(key: String): String? {
        return repository.retrieveValue(key)
    }


}