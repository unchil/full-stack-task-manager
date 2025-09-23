package org.example.ktor.module


import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
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

            get("/seawaterinfo/{division}"){
                val division = call.parameters["division"]

                if (division == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                try {
                    val result = repository.seaWaterInfo(division)
                    if (result.isEmpty()) {
                        call.respond(HttpStatusCode.NotFound)
                        return@get
                    }
                    call.respond(result)
                } catch (ex: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }

            get ("/stat"){

                try {
                    val result = repository.seaWaterInfoStatistics()
                    if (result.isEmpty()) {
                        call.respond(HttpStatusCode.NotFound)
                        return@get
                    }
                    call.respond(result)
                } catch (ex: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest)
                }

            }

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