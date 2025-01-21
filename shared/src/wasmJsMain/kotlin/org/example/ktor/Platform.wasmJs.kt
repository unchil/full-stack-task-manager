package org.example.ktor

import io.ktor.client.engine.js.*

class WasmPlatform: Platform {
    override val name: String = "Web with Kotlin/Wasm"
}


actual fun getPlatform(): Platform = WasmPlatform()

