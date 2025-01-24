package org.example.ktor.db.entity

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.example.ktor.model.*

object TaskTable : Table("task") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 50)
    val description = varchar("description", 50)
    val priority = varchar("priority", 50)

    override val primaryKey: PrimaryKey = PrimaryKey(id, name = "id")
}



fun taskTableToModel(it: ResultRow) = Task(
    it[TaskTable.name],
    it[TaskTable.description],
    Priority.valueOf(it[TaskTable.priority])
)

