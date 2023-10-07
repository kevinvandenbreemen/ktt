package com.vandenbreemen.ktt.persistence

import com.vandenbreemen.kevincommon.db.DatabaseSchema
import com.vandenbreemen.kevincommon.db.SQLiteDAO
import com.vandenbreemen.ktt.message.NoSuchPageError
import com.vandenbreemen.ktt.model.Page
import org.slf4j.LoggerFactory

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

    suspend fun createPage(page: Page) {
        dao.insert("INSERT INTO page(title, content) VALUES(?, ?)",
            arrayOf(page.title, page.content)
        )
    }

    suspend fun loadPage(s: String): Page {

        val raw = dao.query("SELECT title, content FROM page WHERE id=?", arrayOf(s))

        if(raw.isEmpty()) {
            throw NoSuchPageError("Page with id=$s not found")
        }

        return Page(raw[0]["title"] as String, raw[0]["content"] as String)

    }

    fun updatePage(id: String, updated: Page) {
        dao.update("UPDATE page SET title=?, content=? WHERE id=?",
            arrayOf(updated.title, updated.content, id)
        )
    }

}