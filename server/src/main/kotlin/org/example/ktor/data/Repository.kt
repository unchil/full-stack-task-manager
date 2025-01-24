package org.example.ktor.data

import kotlinx.coroutines.Dispatchers
import org.example.ktor.db.entity.TaskTable
import org.example.ktor.db.entity.taskTableToModel
import org.example.ktor.model.*

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class Repository : TaskRepository {

    suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
        newSuspendedTransaction(Dispatchers.IO, statement = block)

    override suspend fun allTasks(): List<Task>  = suspendTransaction {
        TaskTable.selectAll()
            .map(::taskTableToModel)
    }

    override suspend fun tasksByPriority(priority: Priority): List<Task>   = suspendTransaction {
        TaskTable.selectAll()
            .where { (TaskTable.priority eq priority.toString()) }
            .map(::taskTableToModel)
    }

    override suspend fun taskByName(name: String): Task? = suspendTransaction {

        TaskTable.selectAll()
            .where { TaskTable.name eq name }
            .withDistinct()
            .map(::taskTableToModel)
            .firstOrNull()
    }

    override suspend fun addTask(task: Task): Unit = suspendTransaction {
        TaskTable.insert {
            it[name] = task.name
            it[description] = task.description
            it[priority] = task.priority.toString()
        } [TaskTable.id]
    }

    override suspend fun removeTask(name: String): Boolean  = suspendTransaction {
        val rowsDeleted = TaskTable.deleteWhere {
            TaskTable.name eq name
        }
        rowsDeleted == 1
    }

}