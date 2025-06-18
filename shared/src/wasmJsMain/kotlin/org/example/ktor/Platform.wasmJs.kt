package org.example.ktor

import org.example.ktor.data.NifsRepository

class WasmPlatform: Platform {

    override val name: String = "Web with Kotlin/Wasm"

    override val nifsRepository: NifsRepository
        get() = NifsRepository()
}


actual fun getPlatform(): Platform = WasmPlatform()

