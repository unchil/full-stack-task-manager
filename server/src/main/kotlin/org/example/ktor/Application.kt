package org.example.ktor


import io.ktor.server.application.Application
import io.ktor.server.application.log
import org.example.ktor.data.NifsRepository
import org.example.ktor.module.LOGGER
import org.example.ktor.module.configureNifsDatabase
import org.example.ktor.module.configureNifsSerialization


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module_Serialization(){
    LOGGER.info("Start Ktor Server")
  //  log.info("Start Ktor Server")
    configureNifsDatabase()
    configureNifsSerialization(NifsRepository())
}





