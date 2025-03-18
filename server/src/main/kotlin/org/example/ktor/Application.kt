package org.example.ktor


import io.ktor.server.application.*
import org.example.ktor.data.NifsRepository
import org.example.ktor.module.configureNifsDatabase
import org.example.ktor.module.configureNifsSerialization

fun main(args: Array<String>){
    io.ktor.server.netty.EngineMain.main(args)
}


fun Application.module_Serialization(){
    configureNifsDatabase()
    configureNifsSerialization(NifsRepository())
}





