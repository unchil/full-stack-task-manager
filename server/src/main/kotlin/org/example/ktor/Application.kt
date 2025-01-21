package org.example.ktor


import io.ktor.server.application.Application
import org.example.ktor.module.configureRouting

fun main(args: Array<String>){
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module_Routing(){
    configureRouting()
}



