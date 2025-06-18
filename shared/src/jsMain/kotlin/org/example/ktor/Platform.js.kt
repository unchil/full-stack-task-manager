package org.example.ktor

import org.example.ktor.data.NifsRepository

class JsPlatform: Platform {

    override val name: String = "Web with Kotlin/Js"

    override val nifsRepository: NifsRepository
        get() = NifsRepository()
}




actual fun getPlatform(): Platform = JsPlatform()