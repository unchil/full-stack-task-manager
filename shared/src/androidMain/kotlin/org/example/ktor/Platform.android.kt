package org.example.ktor

import android.os.Build
import org.example.ktor.data.NifsRepository
import org.example.ktor.data.Repository

class AndroidPlatform() : Platform {

    override val name: String = "Android ${Build.VERSION.SDK_INT}"

    override val repository: Repository
        get() = Repository()

    override val nifsRepository: NifsRepository
        get() = NifsRepository()

}

actual fun getPlatform(): Platform = AndroidPlatform()

