package org.example.ktor

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.example.ktor.data.Repository
import org.example.ktor.model.Task


class AppViewModel( private val scope: CoroutineScope) {

    private val repository:Repository
        = getPlatform().repository

    private val _tasksStateFlow: MutableStateFlow<List<Task>>
            = MutableStateFlow(emptyList())

    val tasksStateFlow: StateFlow<List<Task>>
            = _tasksStateFlow

    init {
        scope.launch {
            repository.getAllTasks()
            repository._tasksStateFlow.collectLatest {
                _tasksStateFlow.value = it
            }
        }
    }

    suspend fun onEvent(event: Event){
        when(event){
            is Event.GetAllTasks -> {
                getAllTasks()
            }
            is Event.UpdateTask -> {
                updateTask(event.newTask)
            }
            is Event.RemoveTask -> {
                removeTask(event.task)
            }
            else -> {}
        }
    }

    suspend fun getAllTasks(){
        repository.getAllTasks()
    }

    suspend fun updateTask(task: Task){
        repository.updateTask(task)
        repository.getAllTasks()
    }

    suspend fun removeTask(task: Task){
        repository.removeTask(task)
        repository.getAllTasks()
    }



    sealed class  Event{
        data object GetAllTasks : Event()
        data class UpdateTask (val newTask: Task) :Event()
        data class RemoveTask (val task: Task) :Event()
    }

}