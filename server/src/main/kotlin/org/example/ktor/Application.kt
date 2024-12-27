package org.example.ktor


import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.example.ktor.data.InMemoryTaskRepository
import org.example.ktor.plugins.configureSerialization

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "localhost", module = Application::module)
        .start(wait = true)
}

fun Application.module() {

    val repository = InMemoryTaskRepository()

    configureSerialization(repository)


}