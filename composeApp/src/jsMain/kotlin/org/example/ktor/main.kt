package org.example.ktor


import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.example.ktor.data.DATA_DIVISION
import org.example.ktor.data.NifsRepository
import org.example.ktor.model.SeawaterInformationByObservationPoint



fun main() {

    val repository = getPlatform().nifsRepository

    window.onload = {

        setContent(repository, ElementID.ID.LayerBars.division()){
            createContent(ElementID.ID.LayerBars, it)
        }
        setContent(repository, ElementID.ID.BoxPlot.division()){
            createContent(ElementID.ID.BoxPlot, it)
        }

        setContent(repository, ElementID.ID.Line.division()){
            createContent(ElementID.ID.Line, it)
        }

        setContent(repository, ElementID.ID.Ribbon.division()){
            createContent(ElementID.ID.Ribbon, it)
        }
    }

}


fun setContent(
    repository: NifsRepository,
    division: DATA_DIVISION,
    completeHandle:(result:List<Any>)->Unit
) = CoroutineScope(Dispatchers.Default).launch {

    val data = when(division){
        DATA_DIVISION.current, DATA_DIVISION.oneday -> {
            repository.getSeaWaterInfoValues(division.name)
        }
        DATA_DIVISION.statistics -> {
            repository.getSeaWaterInfoStatValues()
        }
        else ->{
            emptyList()
        }
    }

    completeHandle(data)
}



