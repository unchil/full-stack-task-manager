package org.example.ktor


import io.ktor.server.application.Application
import org.example.ktor.data.InMemoryTaskRepository
import org.example.ktor.module.configureRouting
import org.example.ktor.module.configureSerialization

fun main(args: Array<String>){
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module_Routing(){
    configureRouting()
}


fun Application.module_Serialization(){
    val repository = InMemoryTaskRepository()
    configureSerialization(repository)
}





