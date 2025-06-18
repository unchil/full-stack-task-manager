package org.example.ktor

import org.example.ktor.data.NifsRepository

class JVMPlatform: Platform {

    override val name: String = "Java ${System.getProperty("java.version")}"

    override val nifsRepository: NifsRepository
        get() = NifsRepository()
}

actual fun getPlatform(): Platform = JVMPlatform()

