package com.vandenbreemen.ktt.persistence

import com.vandenbreemen.kevincommon.db.DatabaseSchema
import com.vandenbreemen.kevincommon.db.SQLiteDAO
import com.vandenbreemen.ktt.message.NoSuchPageError
import com.vandenbreemen.ktt.model.Page
import com.vandenbreemen.ktt.model.PageSearchResult
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

class SQLiteWikiRepository(private val databasePath: String) {

    private val logger = LoggerFactory.getLogger(SQLiteWikiRepository::class.java)

    private val dao = SQLiteDAO(databasePath)

    /**
     * Tag ID cache
     */
    private val tagIds = ConcurrentHashMap<String, Int>()


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

        schema.addDatabaseChange(2, """
            CREATE UNIQUE INDEX uc_page_title ON page(title)
        """.trimIndent())

        schema.addDatabaseChange(3, """
            CREATE TABLE tag(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL
            );
            
            CREATE UNIQUE INDEX uc_tag_name ON tag(name);
            
            CREATE TABLE page_tag(
                pageId INTEGER NOT NULL,
                tagId INTEGER NOT NULL,
                CONSTRAINT fk_tag_pageid FOREIGN KEY(pageId) REFERENCES page(id) ON DELETE CASCADE,
                CONSTRAINT fk_tag_tagid FOREIGN KEY(tagId) REFERENCES tag(id) ON DELETE CASCADE
            );
            
            CREATE UNIQUE INDEX uc_page_tag ON page_tag(pageId, tagId);
        """.trimIndent())
    }

    fun createPage(page: Page): Int {

        dao.insert("INSERT INTO page(title, content) VALUES(?, ?)",
            arrayOf(page.title, page.content)
        )

        val id = dao.query("SELECT max(id) as 'id' FROM page", emptyArray())[0]["id"]
        return id as Int
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

    /**
     * Find the page whose title matches the given string
     */
    fun searchPageByTitle(title: String): Int? {
        val raw = dao.query("SELECT id FROM page WHERE title=?", arrayOf(title))
        if(raw.isEmpty()) {
            return null
        }
        return raw[0]["id"] as Int
    }

    fun searchPages(searchTerms: String): List<PageSearchResult> {
        val pattern = "%$searchTerms%"
        val raw = dao.query("SELECT id, title, content FROM page WHERE title LIKE ? OR content LIKE ? OR id IN (select pageId FROM page_tag WHERE tagId IN (SELECT id FROM tag WHERE name LIKE ?))", arrayOf(pattern, pattern, pattern))
        return raw.map { row->
            PageSearchResult(row["id"].toString(), row["title"] as String)
        }
    }

    fun addTag(tag: String) {
        dao.insert("INSERT INTO tag(name) SELECT ? WHERE NOT EXISTS (SELECT 1 FROM tag WHERE name=?)", arrayOf(tag, tag))
    }

    fun removeTagsForPage(pageId: String) {
        dao.delete("DELETE FROM page_tag WHERE pageId=?", arrayOf(pageId))
    }

    fun assignPageTag(pageId: String, tagName: String) {
        addTag(tagName)
        val tagId = tagIds[tagName] ?: run {
            val tagId = dao.query("SELECT id FROM tag WHERE name=?", arrayOf(tagName))[0]["id"] as Int
            tagIds[tagName] = tagId
            tagId
        }
        dao.insert("INSERT OR IGNORE INTO page_tag(pageId, tagId) VALUES (?, ?)", arrayOf(pageId, tagId))
    }

    fun getPageTags(pageId: String): List<String> {
        return dao.query("SELECT name FROM tag WHERE id IN (SELECT tagId FROM page_tag WHERE pageId=?)", arrayOf(pageId)).map { row->
            row["name"] as String
        }
    }

}