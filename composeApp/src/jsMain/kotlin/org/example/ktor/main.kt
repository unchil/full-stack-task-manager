package org.example.ktor


import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.example.ktor.data.NifsRepository
import org.example.ktor.model.SeawaterInformationByObservationPoint



fun main() {
    console.log("[console] Hello, Kotlin/JS!")

    val repository = getPlatform().nifsRepository

    window.onload = {

        setContent(repository, "current"){
            createContent("LayerBars", it)
        }
        setContent(repository, "oneday"){
            createContent("BoxPlot", it)
        }

        setContent(repository, "oneday"){
            createContent("Line", it)
        }
        setContent(repository, "stat"){
            createContent("Ribbon", it)
        }
    }

}


fun setContent(
    repository: NifsRepository,
    division:String,
    completeHandle:(result:List<Any>)->Unit
) = CoroutineScope(Dispatchers.Default).launch {

    val data = when(division){
        "current", "oneday" -> {
            repository.getSeaWaterInfoValues(division)
        }
        "stat" -> {
            repository.getSeaWaterInfoStatValues()
        }
        else ->{
            emptyList()
        }
    }

    completeHandle(data)
}



