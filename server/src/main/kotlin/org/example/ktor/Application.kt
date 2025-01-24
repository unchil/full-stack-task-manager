package org.example.ktor


import io.ktor.server.application.Application
import org.example.ktor.data.Repository
import org.example.ktor.module.configureDatabases
import org.example.ktor.module.configureRouting
import org.example.ktor.module.configureSerialization

fun main(args: Array<String>){
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module_Routing(){
    configureRouting()
}

fun Application.module_Serialization(){
    configureDatabases()
    val repository = Repository()
    configureSerialization(repository)
}





