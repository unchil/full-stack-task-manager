package org.example.ktor

import org.example.ktor.data.NifsRepository
import platform.UIKit.UIDevice

class IOSPlatform: Platform {

    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion


    override val nifsRepository: NifsRepository
        get() = NifsRepository()
}

actual fun getPlatform(): Platform = IOSPlatform()

