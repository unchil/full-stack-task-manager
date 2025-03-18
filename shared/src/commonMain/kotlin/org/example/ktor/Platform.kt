package org.example.ktor

import org.example.ktor.data.NifsRepository
import org.example.ktor.data.Repository

interface Platform {
    val name: String
    val repository: Repository
    val nifsRepository: NifsRepository
}

expect fun getPlatform(): Platform
