package org.example.ktor


import kotlinx.dom.clear
import org.example.ktor.SEA_AREA.gru_nam
import org.example.ktor.data.DATA_DIVISION
import org.jetbrains.letsPlot.frontend.JsFrontendUtil
import org.w3c.dom.Element
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.label
import react.useEffect
import react.useState
import web.cssom.ClassName
import web.html.InputType
import kotlin.js.Json

external interface SeaWaterInfoChartProps : Props {
	var initialSelectedSea: String
	var chartDiv: Element?
	var dataDivision : DATA_DIVISION
	var loadDataFunction: suspend (DATA_DIVISION) -> List<Any>
	var createChartFunction: ( Map<String, List<Any>>, String?) -> org.jetbrains.letsPlot.intern.Plot
	var chartDataMapper: ( List<Any>, String?) -> Map<String, List<Any>>
}

external interface SeaWaterInfoDataGridProps : Props {

	var chartDiv: Element?
	var dataDivision : DATA_DIVISION
	var loadDataFunction: suspend (DATA_DIVISION) -> List<Any>
	var createDataGridFunction: (Element, Array<Json>) -> Unit
	var gridDataMapper: ( List<Any> ) -> Array<Json>
}

val SeaWaterInfoDataGrid = FC<SeaWaterInfoDataGridProps> { props ->
	useEffect {
		props.chartDiv?.let { currentDiv ->
			currentDiv.clear()
			props.loadDataFunction(props.dataDivision).let { it ->
				props.createDataGridFunction( currentDiv, props.gridDataMapper(it) )
			}
		}
	}
}


val SeaWaterInfoChart = FC<SeaWaterInfoChartProps> { props ->
	useEffect {
		props.chartDiv?.let { currentDiv ->
			currentDiv.clear()
			props.loadDataFunction(props.dataDivision).let { it ->
				currentDiv.appendChild(
					JsFrontendUtil.createPlotDiv(
						props.createChartFunction( props.chartDataMapper( it, null), null )
					)
				)
			}
		}
	}
}

val SeaAreaLineChart = FC<SeaWaterInfoChartProps> { props ->

	var selectedSea by useState(props.initialSelectedSea)

	val handleSeaChange = { newSeaName: String ->
		selectedSea = SEA_AREA.GRU_NAME.entries.findLast{ it.name == newSeaName }?.name ?: SEA_AREA.GRU_NAME.entries[0].name
	}

	useEffect(selectedSea) {

		props.chartDiv?.let { currentDiv ->
			currentDiv.clear()
			props.loadDataFunction(props.dataDivision).let { it ->
				currentDiv.appendChild(
					JsFrontendUtil.createPlotDiv(
						props.createChartFunction(
							props.chartDataMapper(it, SEA_AREA.GRU_NAME.entries.findLast{ it.name == selectedSea }?.gru_nam()) ,
							selectedSea
						)
					)
				)
			}
		}

	}

	div {
		className = ClassName("horizontal-div")

		SEA_AREA.GRU_NAME.entries.forEach { seaEnumEntry ->
			div {
				key = seaEnumEntry.name // React 리스트 렌더링을 위한 key

				input {
					type = InputType.radio
					id = "line_chart_sea_${seaEnumEntry.name}" // 고유 ID
					name = "seaAreaRadioBtnGroupLine"        // 그룹명
					value = seaEnumEntry.name
					checked = selectedSea == seaEnumEntry.name
					onChange = { event ->
						handleSeaChange(event.target.value)
					}
				}
				label {
					htmlFor = "line_chart_sea_${seaEnumEntry.name}"
					+seaEnumEntry.name
				}
			}
		}
	}
}


val SeaAreaRibbonChart = FC<SeaWaterInfoChartProps> { props ->

	var selectedSea by useState(props.initialSelectedSea)

	val handleSeaChange = { newSeaName: String ->
		selectedSea = SEA_AREA.GRU_NAME.entries.findLast{ it.name == newSeaName }?.name ?: SEA_AREA.GRU_NAME.entries[0].name
	}

	useEffect(selectedSea) {

		props.chartDiv?.let { currentDiv ->
			currentDiv.clear()
			props.loadDataFunction(props.dataDivision).let { it ->
				currentDiv.appendChild(
					JsFrontendUtil.createPlotDiv(
						props.createChartFunction(
							props.chartDataMapper(it, SEA_AREA.GRU_NAME.entries.findLast{ it.name == selectedSea }?.gru_nam()),
							selectedSea
						)
					)
				)
			}
		}
	}

	div {
		className = ClassName("horizontal-div")

		SEA_AREA.GRU_NAME.entries.forEach { seaEnumEntry ->
			div {
				key = seaEnumEntry.name

				input {
					type = InputType.radio
					id = "ribbon_chart_sea_${seaEnumEntry.name}"
					name = "ribbonAreaRadioBtnGroupLine"
					value = seaEnumEntry.name
					checked = selectedSea == seaEnumEntry.name
					onChange = { event ->
						handleSeaChange(event.target.value)
					}
				}
				label {
					htmlFor = "ribbon_chart_sea_${seaEnumEntry.name}"
					+seaEnumEntry.name
				}
			}
		}
	}
}



