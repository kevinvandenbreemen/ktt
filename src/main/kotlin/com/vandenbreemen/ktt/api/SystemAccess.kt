package com.vandenbreemen.ktt.api

/**
 * Store and retrieve values from the underlying wiki system
 */
interface SystemAccess {

    fun storeValue(key: String, value: String)

    fun retrieveValue(key: String): String?


}