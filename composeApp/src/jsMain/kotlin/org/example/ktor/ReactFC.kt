package org.example.ktor

import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.label
import web.html.InputType

import web.cssom.ClassName

external interface SeaSelectionProps : Props {
    var selectedSea: String
    var onSelect: (String) -> Unit
}

val SeaSelection = FC<SeaSelectionProps> { props ->
    val seas = SEA_AREA.GRU_NAME.entries
    div {

        className = ClassName("horizontal-div") // Corrected line

        seas.forEach { it ->
            div {
                this.asDynamic().key = it // sea 값을 key로 사용 (고유하다면)

                input {
                    type = InputType.radio
                    id = it.name
                    name = "seaAreaRadioBtnGroup" // 모든 라디오 버튼에 동일한 name 부여
                    value = it
                    checked = props.selectedSea == it.name
                    onChange = {
                        props.onSelect(it.target.value)
                    }
                }
                label {
                    htmlFor = it.name
                    +it.name
                }
            }
        }
    }
}


val RibbonSelection = FC<SeaSelectionProps> { props ->
    val seas = SEA_AREA.GRU_NAME.entries
    div {

        className = ClassName("horizontal-div") // Corrected line

        seas.forEach { it ->
            div {
                this.asDynamic().key = it   // sea 값을 key로 사용 (고유하다면)

                input {
                    type = InputType.radio
                    id = it.name
                    name = "ribbonAreaRadioBtnGroup" // 모든 라디오 버튼에 동일한 name 부여
                    value = it
                    checked = props.selectedSea == it.name
                    onChange = {
                        props.onSelect(it.target.value)
                    }
                }
                label {
                    htmlFor = it.name
                    +it.name
                }
            }
        }
    }
}