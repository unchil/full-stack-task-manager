package org.example.ktor


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch
import org.example.ktor.model.Priority
import org.example.ktor.model.Task


@Composable
fun App() {

    MaterialTheme {

        val scope = rememberCoroutineScope()
        var currentTask by remember { mutableStateOf<Task?>(null) }

        val viewModel = remember {
            AppViewModel(scope)
        }

        LaunchedEffect(key1 = viewModel) {
            viewModel.onEvent(
                AppViewModel.Event.GetAllTasks
            )
        }

        val tasks = viewModel.tasksStateFlow.collectAsState().value


        if (currentTask != null) {
            UpdateTaskDialog(
                currentTask!!,
                onConfirm = {
                    scope.launch {
                        viewModel.onEvent(AppViewModel.Event.UpdateTask(it))
                    }

                    currentTask = null
                }
            )
        }


        LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            items(tasks.count()) { index ->
                TaskCard(
                    tasks[index],
                    onDelete = {
                        scope.launch {
                            viewModel.onEvent(AppViewModel.Event.RemoveTask(it))
                        }
                    },
                    onUpdate = {
                        currentTask = tasks[index]
                    }
                )
            }
        }


    }
}

@Composable
fun TaskCard(
    task: Task,
    onDelete: (Task) -> Unit,
    onUpdate: (Task) -> Unit
) {

    fun pickWeight(priority: Priority) = when (priority) {
        Priority.Low -> FontWeight.SemiBold
        Priority.Medium -> FontWeight.Bold
        Priority.High, Priority.Vital -> FontWeight.ExtraBold
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(4.dp),
        shape = RoundedCornerShape(CornerSize(4.dp))
    ) {

        Column(modifier = Modifier.padding(10.dp)) {

            Text(
                "${task.name}: ${task.description}",
                fontSize = 20.sp,
                fontWeight = pickWeight(task.priority)
            )

            Row {
                OutlinedButton(onClick = { onDelete(task) }) {
                    Text("Delete")
                }
                Spacer(Modifier.width(8.dp))

                OutlinedButton(onClick = { onUpdate(task) }) {
                    Text("Update")
                }
            }
        }
    }

}

@Composable
fun UpdateTaskDialog(
    task: Task,
    onConfirm: (Task) -> Unit
) {
    var description by remember { mutableStateOf(task.description) }
    var priorityText by remember { mutableStateOf(task.priority.toString()) }
    val colors = TextFieldDefaults.textFieldColors(
        backgroundColor = Color.White,
        textColor = Color.Blue
    )

    Dialog(onDismissRequest = {}) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(4.dp),
            shape = RoundedCornerShape(CornerSize(4.dp))
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text("Update ${task.name}", fontSize = 20.sp)
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    colors = colors
                )
                TextField(
                    value = priorityText,
                    onValueChange = { priorityText = it },
                    label = { Text("Priority") },
                    colors = colors
                )
                OutlinedButton(onClick = {
                    val newTask = Task(
                        task.name,
                        description,
                        try {
                            Priority.valueOf(priorityText)
                        } catch (e: IllegalArgumentException) {
                            Priority.Low
                        }
                    )
                    onConfirm(newTask)
                }) {
                    Text("Update")
                }
            }
        }
    }
}