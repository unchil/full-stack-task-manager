package org.example.ktor.module


import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.ktor.data.NifsRepository

fun Application.configureNifsSerialization(repository: NifsRepository) {

    install(ContentNegotiation) {
        json()
    }

    install(DefaultHeaders){
        header("Access-Control-Allow-Origin", "*")
    }

    routing{
        get("/") {
            call.respondText("Beautiful World!")
        }

        route("/nifs") {

            get("/observations/{division}"){
                val division = call.parameters["division"]
                if (division == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }

                try {
                    val result = repository.observationList(division)
                    if (result.isEmpty()) {
                        call.respond(HttpStatusCode.NotFound)
                        return@get
                    }
                    call.respond(result)

                } catch (ex: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest)
                }

            }


            get ("/observatory"){
                try {
                    val result = repository.observatoryInfo()
                    if (result.isEmpty()) {
                        call.respond(HttpStatusCode.NotFound)
                        return@get
                    }
                    call.respond(result)

                } catch (ex: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest)
                }

            }

        }

    }

}