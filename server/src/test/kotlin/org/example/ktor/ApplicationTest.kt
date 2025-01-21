package org.example.ktor

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            module_Routing()
        }
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Beautiful World!", response.bodyAsText())
    }


    @Test
    fun testConfig() = testApplication {
        environment {
            config = ApplicationConfig("application.yaml")
        }

    }

}