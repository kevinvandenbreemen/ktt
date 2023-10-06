package com.vandenbreemen.ktt.persistence

import com.vandenbreemen.kevincommon.db.DatabaseSchema
import com.vandenbreemen.kevincommon.db.SQLiteDAO
import com.vandenbreemen.ktt.message.NoSuchPageError
import com.vandenbreemen.ktt.model.Page
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture

class SQLiteWikiRepository(private val databasePath: String) {

    private val logger = LoggerFactory.getLogger(SQLiteWikiRepository::class.java)

    private val dao = SQLiteDAO(databasePath)

    init {
        initializeDatabase()
    }

    private fun initializeDatabase() {
        val schema = DatabaseSchema(dao)
        schema.addDatabaseChange(1, """
            CREATE TABLE page(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                content TEXT NOT NULL
            )
        """.trimIndent())
    }

    fun createPage(page: Page) {
        CoroutineScope(Dispatchers.IO).launch {
            dao.insert("INSERT INTO page(title, content) VALUES(?, ?)",
                arrayOf(page.title, page.content)
            )
        }
    }

    fun loadPage(s: String): CompletableFuture<Page> {

        val promise = CompletableFuture<Page>()

        CoroutineScope(Dispatchers.IO).launch {
            val raw = dao.query("SELECT title, content FROM page WHERE id=?", arrayOf(s))

            if(raw.isEmpty()) {
                promise.completeExceptionally(NoSuchPageError("Page with id=$s not found"))
                return@launch
            }

            promise.complete(Page(raw[0]["title"] as String, raw[0]["content"] as String))
        }

        return promise

    }

}