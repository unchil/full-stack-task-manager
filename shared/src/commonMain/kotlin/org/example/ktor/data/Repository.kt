package org.example.ktor.data

import kotlinx.coroutines.flow.MutableStateFlow
import org.example.ktor.model.Task
import org.example.ktor.network.TaskApi

class Repository {

    private val taskApi = TaskApi()

    val _tasksStateFlow: MutableStateFlow<List<Task>>
            = MutableStateFlow( emptyList())


    suspend fun getAllTasks() {
        try {
            _tasksStateFlow.value = taskApi.getAllTasks()
        } catch (e:Exception){

        }
    }

    suspend fun updateTask(newTask: Task) {
        taskApi.updateTask(newTask)

    }

    suspend fun removeTask(task: Task) {
        taskApi.removeTask(task)

    }
}