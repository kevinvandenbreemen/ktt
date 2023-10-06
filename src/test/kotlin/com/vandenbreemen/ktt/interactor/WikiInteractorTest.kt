package com.vandenbreemen.ktt.interactor

import kotlinx.coroutines.test.runTest
import org.amshove.kluent.`should not be null`
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class WikiInteractorTest {

    val testWikiInteractor = TestWikiInteractor()
    val wikiInteractor = WikiInteractor(testWikiInteractor)

    @Test
    fun `should delegate to the test wiki interactor when test page is requested`() = runTest {
        val page = wikiInteractor.fetchPage("test")
        page.`should not be null`()
    }

}