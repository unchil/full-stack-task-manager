package org.example.ktor


import kotlinx.dom.clear
import org.example.ktor.SEA_AREA.gru_nam
import org.example.ktor.data.DATA_DIVISION
import org.jetbrains.letsPlot.frontend.JsFrontendUtil
import org.w3c.dom.Element
import react.FC
import react.Props
import react.dom.events.ChangeEvent
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.label
import react.useEffect
import react.useState
import web.cssom.ClassName
import web.html.HTMLInputElement
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

external interface RadioButtonProps : Props {
	var name: String
	var value: String
	var label: String
	var checked: Boolean
	var onChange: (Event: ChangeEvent<HTMLInputElement>) -> Unit
	var id:String
}

val RadioButton = FC<RadioButtonProps> { props ->
	input {
		type = InputType.radio
		name = props.name
		value = props.value
		checked = props.checked
		onChange = props.onChange
		id = props.id // 라벨 클릭 시 라디오 버튼이 선택되도록 연결( input 의 id 와 label의 id 가 같아야 함)
	}
	label {
		htmlFor = props.id  // 라벨 클릭 시 라디오 버튼이 선택되도록 연결( input 의 id 와 label의 id 가 같아야 함)
		+props.label
	}
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

	val handleOptionChange: (Event:ChangeEvent<HTMLInputElement>) -> Unit = { event ->
		val newSeaName = event.target.asDynamic().value as String
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
				RadioButton {
					name = "RadioGroup_line"+ props.chartDiv?.id  // 그룹별 고유 네임으로 설정( checked 관련 중요  )
					id = seaEnumEntry.name + props.chartDiv?.id
					value = seaEnumEntry.name
					label = seaEnumEntry.name
					checked = selectedSea == seaEnumEntry.name
					onChange = handleOptionChange

				}
			}
		}
	}
}

