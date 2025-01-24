package org.example.ktor

import io.ktor.server.config.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {


    @Test
    fun testConfig() = testApplication {
        environment {
            config = ApplicationConfig("application.yaml")
            assertEquals( config.property("ktor.deployment.port").getString(), "7788")
            assertEquals( config.property("ktor.deployment.host").getString(), "127.0.0.1")
        }
    }


}