package com.vandenbreemen.ktt.interactor

import com.vandenbreemen.ktt.persistence.SQLiteWikiRepository
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.io.File

class ConfigurationInteractorTest {

    val path = "config_tst.dat"
    val configurationInteractor = ConfigurationInteractor(SQLiteWikiRepository(path))

    @AfterEach
    fun tearDown() {
        File(path).delete()
    }

    @Test
    fun `should retrieve port`() {
        configurationInteractor.getPort() shouldBeEqualTo 8080
    }

}