package com.vandenbreemen.ktt.interactor

import com.vandenbreemen.ktt.persistence.SQLiteWikiRepository
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.io.File

class SystemAccessInteractorTest {

    val filename = "sysaccess.dat"
    val interactor = SystemAccessInteractor(SQLiteWikiRepository(filename))


    @AfterEach
    fun tearDown() {
        File(filename).delete()
    }

    @Test
    fun `should store a configuration item`() {

        interactor.storeValue("key1", "this is a test")
        val stored = interactor.retrieveValue("key1")
        stored shouldBeEqualTo "this is a test"

    }

    @Test
    fun `should replace a configuration value`() {
        interactor.storeValue("key1", "this is a test")
        interactor.storeValue("key1", "replaced value")
        val stored = interactor.retrieveValue("key1")
        stored shouldBeEqualTo "replaced value"
    }

}