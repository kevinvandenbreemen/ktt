package com.vandenbreemen.ktt.interactor

import com.vandenbreemen.ktt.persistence.SQLiteWikiRepository
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.io.File

internal class ConfigurationInteractorTest {

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

    @Test
    fun `should update the port`() {

        val configuration = configurationInteractor.getUIConfiguration()
        with(configuration.copy(runPort = 8000)){
            configurationInteractor.updateUIConfiguration(this)
        }

        configurationInteractor.getPort() shouldBeEqualTo 8000
    }

}