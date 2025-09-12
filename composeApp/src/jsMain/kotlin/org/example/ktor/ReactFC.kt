package org.example.ktor


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

external interface SeaAreaRadioBtnProps : Props {
    var initialSelectedSea: SEA_AREA.GRU_NAME
    var chartDiv: Element?
    var chartData: List<Any> // 차트 그리기에 필요한 전체 데이터
    var createLineChartFunction: (SEA_AREA.GRU_NAME, Map<String, List<Any>>) -> org.jetbrains.letsPlot.intern.Plot // 함수 타입 명시
    var lineChartDataMapper: (SEA_AREA.GRU_NAME, List<Any>) -> Map<String, List<Any>> // 데이터 매핑 함수 타입
}

val SeaAreaRadioBtn = FC<SeaAreaRadioBtnProps> { props ->
    // useState 훅을 사용하여 선택된 바다를 상태로 관리
    var selectedSea by useState(props.initialSelectedSea)

    // 선택 변경 시 호출될 함수
    val handleSeaChange = { newSeaName: String ->
        val newSelectedSeaEnum = SEA_AREA.GRU_NAME.entries.find { it.name == newSeaName }
        newSelectedSeaEnum?.let {
            selectedSea = it // 상태 업데이트 -> 리렌더링 유발
        }
    }

    // 차트 업데이트 로직 (useEffect 사용)
    // selectedSea 상태가 변경될 때마다 차트를 다시 그림
    useEffect(selectedSea) {

        props.chartDiv?.let {
            // 기존 차트 내용 지우기
            while (it.firstChild != null) {
                it.removeChild(it.firstChild!!)
            }
            // 새 차트 추가
            it.appendChild(
            JsFrontendUtil.createPlotDiv(
                props.createLineChartFunction(
                        selectedSea,
                        props.lineChartDataMapper(selectedSea, props.chartData) )
                )
            )
        }
    }

    div {
        className = ClassName("horizontal-div") // 수평 정렬을 위한 클래스 (CSS 필요)

        SEA_AREA.GRU_NAME.entries.forEach { seaEnumEntry ->
            div {
                key = seaEnumEntry.name // React 리스트 렌더링을 위한 key

                input {
                    type = InputType.radio
                    id = "line_chart_sea_${seaEnumEntry.name}" // 고유 ID
                    name = "seaAreaRadioBtnGroupLine"        // 그룹명
                    value = seaEnumEntry.name
                    checked = selectedSea == seaEnumEntry
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


val RibbonAreaRadioBtn = FC<SeaAreaRadioBtnProps> { props ->

    var selectedSea by useState(props.initialSelectedSea)

    val handleSeaChange = { newSeaName: String ->
        val newSelectedSeaEnum = SEA_AREA.GRU_NAME.entries.find { it.name == newSeaName }
        newSelectedSeaEnum?.let {
            selectedSea = it
        }
    }

    useEffect(selectedSea) {
        val lineDiv = props.chartDiv

        lineDiv?.let {
            while (it.firstChild != null) {
                it.removeChild(it.firstChild!!)
            }

            it.appendChild(
                JsFrontendUtil.createPlotDiv(
                    props.createLineChartFunction(
                        selectedSea,
                        props.lineChartDataMapper(selectedSea, props.chartData)
                    )
                )
            )
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
                    checked = selectedSea == seaEnumEntry
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



