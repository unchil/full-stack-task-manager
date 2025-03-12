package org.example.ktor

suspend fun main() {

    val repository = Repository()
    repository.getRealTimeObservation()
    repository.getRealTimeObservatory()

    NifsApi.client.close()
}