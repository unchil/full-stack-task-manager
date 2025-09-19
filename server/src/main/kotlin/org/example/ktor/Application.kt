package org.example.ktor


import io.ktor.server.application.Application
import org.example.ktor.data.NifsRepository
import org.example.ktor.module.LOGGER
import org.example.ktor.module.configureNifsDatabase
import org.example.ktor.module.configureNifsSerialization


fun main(args: Array<String>){
    LOGGER.info("Start Ktor Server")
    io.ktor.server.netty.EngineMain.main(args)

}

fun Application.module_Serialization(){
    configureNifsDatabase()
    configureNifsSerialization(NifsRepository())
}





