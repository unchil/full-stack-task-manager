package org.example.ktor


import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.io.*
import kotlin.io.path.Path




suspend fun main() {

    val repository = Repository()
    repository.getRealTimeObservation()
    repository.getRealTimeObservatory()

    NifsApi.client.close()
}