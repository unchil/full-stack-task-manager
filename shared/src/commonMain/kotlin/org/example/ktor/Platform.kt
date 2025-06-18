package org.example.ktor

import org.example.ktor.data.NifsRepository

interface Platform {
    val name: String

    val nifsRepository: NifsRepository
}

expect fun getPlatform(): Platform
