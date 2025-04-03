package org.example.ktor

import org.example.ktor.data.NifsRepository
import org.example.ktor.data.Repository

class JsPlatform: Platform {

    override val name: String = "Web with Kotlin/Js"

    override val repository: Repository
        get() = Repository()

    override val nifsRepository: NifsRepository
        get() = NifsRepository()
}




actual fun getPlatform(): Platform = JsPlatform()