package org.example.ktor

import org.example.ktor.data.Repository
import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override val repository: Repository
        get() = Repository()
}

actual fun getPlatform(): Platform = IOSPlatform()

