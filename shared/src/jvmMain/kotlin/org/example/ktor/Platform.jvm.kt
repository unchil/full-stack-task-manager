package org.example.ktor

import org.example.ktor.data.Repository

class JVMPlatform: Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
    override val repository: Repository
        get() = Repository()
}

actual fun getPlatform(): Platform = JVMPlatform()

