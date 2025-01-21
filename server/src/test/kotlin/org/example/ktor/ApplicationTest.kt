package org.example.ktor

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

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
            assertEquals( config.property("ktor.deployment.port").getString(), "7788")
            assertEquals( config.property("ktor.deployment.host").getString(), "127.0.0.1")
        }
    }


}