package org.example.ktor.data

import org.example.ktor.model.Priority
import org.example.ktor.model.Task

class InMemoryTaskRepository : TaskRepository {
    private var tasks = listOf(
        Task("Cleaning", "Clean the house", Priority.Low),
        Task("Gardening", "Mow the lawn", Priority.Medium),
        Task("Shopping", "Buy the groceries", Priority.High),
        Task("Painting", "Paint the fence", Priority.Low),
        Task("Cooking", "Cook the dinner", Priority.Medium),
        Task("Relaxing", "Take a walk", Priority.High),
        Task("Exercising", "Go to the gym", Priority.Low),
        Task("Learning", "Read a book", Priority.Medium),
        Task("Snoozing", "Go for a nap", Priority.High),
        Task("Socializing", "Go to a party", Priority.High)
    )

    override suspend fun allTasks(): List<Task> = tasks

    override suspend fun tasksByPriority(priority: Priority) = tasks.filter {
        it.priority == priority
    }

    override suspend fun taskByName(name: String) = tasks.find {
        it.name.equals(name, ignoreCase = true)
    }

    override suspend fun addTask(task: Task) {
        var notFound = true

        tasks = tasks.map {
            if (it.name == task.name) {
                notFound = false
                task
            } else {
                it
            }
        }
        if (notFound) {
            tasks = tasks.plus(task)
        }
    }

    override suspend fun removeTask(name: String): Boolean {
        val oldTasks = tasks
        tasks = tasks.filterNot { it.name == name }
        return oldTasks.size > tasks.size
    }
}