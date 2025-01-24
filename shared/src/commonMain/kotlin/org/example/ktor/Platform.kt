package org.example.ktor

import org.example.ktor.data.Repository

interface Platform {
    val name: String
    val repository: Repository
}

expect fun getPlatform(): Platform
