package org.example.ktor


import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.io.*
import kotlin.io.path.Path




suspend fun main() {

    val repository = Repository()
    repository.getRealTimeObservation()
    repository.getRealTimeObservatory()

    val df = DataFrame.read("http://127.0.0.1:7788/nifs/seawaterinfo/current")
    val gru_nam by columnGroup()
    df.filter { it[gru_nam].equals("동해") }
        .toStandaloneHTML(DisplayConfiguration(rowsLimit = null))
        .writeHTML(Path("/Volumes/WorkSpace/Dev/full-stack-task-manager/composeApp/src/jsMain/resources/df_rendering.html"))

    NifsApi.client.close()
}