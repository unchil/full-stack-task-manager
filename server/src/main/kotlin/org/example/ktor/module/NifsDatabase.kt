package org.example.ktor.module

import io.ktor.server.application.Application
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureNifsDatabase() {

    val databaseName = environment.config.property("storage.dbName").getString()

    val database = with(databaseName) {
        when{
            startsWith("h2") -> {
                val driver = environment.config.property("storage.database.h2.driverClassName").getString()
                val url = environment.config.property("storage.database.h2.jdbcURL").getString()
                val user = environment.config.property("storage.database.h2.user").getString()
                val password = environment.config.property("storage.database.h2.password").getString()
                Database.connect(
                    url = url,
                    driver = driver,
                    user = user,
                    password = password
                )
            }
            startsWith("sqlite") -> {
                val url = environment.config.property("storage.database.sqlite.jdbcURL").getString()
                val driver = environment.config.property("storage.database.sqlite.driverClassName").getString()
                Database.connect(
                    url = url,
                    driver = driver
                )
            }
            else -> {
                val driver = environment.config.property("storage.database.h2.driverClassName").getString()
                val url = environment.config.property("storage.database.h2.jdbcURL").getString()
                val user = environment.config.property("storage.database.h2.user").getString()
                val password = environment.config.property("storage.database.h2.password").getString()
                Database.connect(
                    url = url,
                    driver = driver,
                    user = user,
                    password = password
                )

            }
        }
    }

    fun initMemoryDb(db:Database){
        transaction(db) {
            addLogger(DBSqlLogger)
        }
    }

    fun initSqliteDbTable(db:Database){
        transaction (db){
            addLogger(DBSqlLogger)
        }
    }

    with(databaseName) {
        when {
            startsWith("h2") -> {
                initMemoryDb(database)
            }
            startsWith("sqlite") -> {
                initSqliteDbTable(database)
            }
            else -> {
                initMemoryDb(database)
            }
        }

    }
}

