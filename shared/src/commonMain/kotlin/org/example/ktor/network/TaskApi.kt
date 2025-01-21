package org.example.ktor.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.example.ktor.getPlatform
import org.example.ktor.model.Task
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.HttpTimeout

class TaskApi {

    private val defaultURL = "http://${if( getPlatform().name.contains("Android") ) "10.0.2.2" else "localhost"}:7788"

    private val httpClient = HttpClient {

        install(ContentNegotiation) {
            json(Json {
                encodeDefaults = true
                isLenient = true
                coerceInputValues = true
                ignoreUnknownKeys = true
            })
        }

        install(Logging) {
            logger = Logger.DEFAULT
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 10000
            connectTimeoutMillis = 3000
            socketTimeoutMillis = 3000
        }
    }

    suspend fun getAllTasks(): List<Task> {
        val url = "${defaultURL}/tasks"
        return httpClient.get(url).body()
    }

    suspend fun removeTask(task: Task) {
        val url = "${defaultURL}/tasks/${task.name}"
        httpClient.delete(url)
    }

    suspend fun updateTask(task: Task) {
        val url = "${defaultURL}/tasks"
        httpClient.post(url) {
            contentType(ContentType.Application.Json)
            setBody(task)
        }
    }
}