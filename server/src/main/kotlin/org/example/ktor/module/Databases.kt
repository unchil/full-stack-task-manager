package org.example.ktor.module

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

import org.example.ktor.db.entity.TaskTable

fun Application.configureDatabases() {

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

    with(databaseName) {
        when {
            startsWith("h2") -> {
                initMemoryDb(database)
            }
            startsWith("sqlite") -> {
                initSqliteDbTable( (database as Database))
            }
            else -> {
                initMemoryDb(database)
            }
        }

    }



}

fun initMemoryDb(db:Database){
    transaction(db) {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(TaskTable)
        initTaskTable()
    }
}

fun initSqliteDbTable(db:Database){
    transaction (db){
        addLogger(StdOutSqlLogger)
        /*
        SchemaUtils.drop( TaskTable)
        SchemaUtils.create( TaskTable)
        initTaskTable()
         */
    }
}


fun initTaskTable(){

    TaskTable.insert {
        it[name] = "Cleaning"
        it[description] = "Clean the house"
        it[priority] = "Low"
    } [TaskTable.id]

    TaskTable.insert {
        it[name] = "Gardening"
        it[description] = "Mow the lawn"
        it[priority] = "Medium"
    } [TaskTable.id]

    TaskTable.insert {
        it[name] = "Shopping"
        it[description] = "Buy the groceries"
        it[priority] = "High"
    } [TaskTable.id]

    TaskTable.insert {
        it[name] = "Painting"
        it[description] = "Paint the fence"
        it[priority] = "Low"
    } [TaskTable.id]

    TaskTable.insert {
        it[name] = "Cooking"
        it[description] = "Cook the dinner"
        it[priority] = "Medium"
    } [TaskTable.id]

    TaskTable.insert {
        it[name] = "Relaxing"
        it[description] = "Take a walk"
        it[priority] = "High"
    } [TaskTable.id]

    TaskTable.insert {
        it[name] = "Exercising"
        it[description] = "Go to the gym"
        it[priority] = "Low"
    } [TaskTable.id]

    TaskTable.insert {
        it[name] = "Learning"
        it[description] = "Read a book"
        it[priority] = "Medium"
    } [TaskTable.id]

    TaskTable.insert {
        it[name] = "Snoozing"
        it[description] = "Go for a nap"
        it[priority] = "High"
    } [TaskTable.id]


    TaskTable.insert {
        it[name] = "Socializing"
        it[description] = "Go to a party"
        it[priority] = "High"
    } [TaskTable.id]


    TaskTable.insert {
        it[name] = "Meditating"
        it[description] = "Contemplate the infinite"
        it[priority] = "High"
    } [TaskTable.id]


}
