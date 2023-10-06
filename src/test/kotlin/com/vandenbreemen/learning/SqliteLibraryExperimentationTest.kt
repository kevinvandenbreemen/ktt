package com.vandenbreemen.learning

import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import java.sql.Connection
import java.sql.DriverManager


/**
 * Test to demonstrate all use cases needed for SQLite for this project
 */
class SqliteLibraryExperimentationTest {

    @Test
    fun `should create a table`() {

        val connection: Connection = DriverManager.getConnection("jdbc:sqlite::memory:")
        connection.createStatement().run {
            executeUpdate("CREATE TABLE test(id, name)")
        }

    }

    @Test
    fun `should provide for multistatement commits`() {
        val connection: Connection = DriverManager.getConnection("jdbc:sqlite::memory:").also { connection->
            connection.autoCommit = false
        }
        connection.createStatement().run {
            executeUpdate("CREATE TABLE test(id, name)")
            executeUpdate("INSERT INTO test(id, name) VALUES (1, 'test')")
        }

        connection.commit()
    }

    @Test
    fun `should provide for multiline statements`() {
        val connection: Connection = DriverManager.getConnection("jdbc:sqlite::memory:").also { connection->
            connection.autoCommit = false
        }
        connection.createStatement().run {
            executeUpdate("""
                CREATE TABLE test(
                    id INT PRIMARY KEY NOT NULL,
                    name TEXT NOT NULL
                )
            """.trimIndent())
            executeUpdate("INSERT INTO test(id, name) VALUES (1, 'test')")
        }

        connection.commit()
    }

    @Test
    fun `should fetch data`() {
        val connection: Connection = DriverManager.getConnection("jdbc:sqlite::memory:").also { connection->
            connection.autoCommit = false
        }
        connection.createStatement().use {
            it.executeUpdate("""
                CREATE TABLE test(
                    id INT PRIMARY KEY NOT NULL,
                    name TEXT NOT NULL
                )
            """.trimIndent())
            it.executeUpdate("INSERT INTO test(id, name) VALUES (1, 'test'), (2, 'bazinga')")
        }

        connection.commit()

        val listResult: MutableList<Map<String, Any>> = mutableListOf()
        connection.createStatement().use {
            val result = it.executeQuery("SELECT id, name FROM test")
            while(result.next()) {
                mutableMapOf<String, Any>().run {
                    put("id", result.getString("id"))
                    put("name", result.getString("name"))

                    listResult.add(this)
                }
            }
        }

        println(listResult)
        listResult.size `should be equal to`(2)
    }

}