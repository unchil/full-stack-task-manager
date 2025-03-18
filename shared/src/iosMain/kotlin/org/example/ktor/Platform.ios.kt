package org.example.ktor

import org.example.ktor.data.NifsRepository
import org.example.ktor.data.Repository
import platform.UIKit.UIDevice

class IOSPlatform: Platform {

    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion

    override val repository: Repository
        get() = Repository()

    override val nifsRepository: NifsRepository
        get() = NifsRepository()
}

actual fun getPlatform(): Platform = IOSPlatform()

