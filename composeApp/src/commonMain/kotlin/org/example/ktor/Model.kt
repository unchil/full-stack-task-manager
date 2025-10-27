package org.example.ktor

object SEA_AREA {

    enum class GRU_NAME {
        WEST, EAST, SOUTH
    }

    fun GRU_NAME.gru_nam():String {
        return when(this) {
            GRU_NAME.WEST -> "서해"
            GRU_NAME.EAST -> "동해"
            GRU_NAME.SOUTH -> "남해"
        }
    }
}


object WATER_QUALITY {
    enum class QualityType{
        rtmWtchWtem, rtmWqCndctv, ph, rtmWqDoxn, rtmWqTu, rtmWqChpla, rtmWqSlnty
    }
    fun QualityType.name():String{
        return when(this) {
            QualityType.rtmWtchWtem -> "수온"
            QualityType.rtmWqCndctv -> "전기전도도"
            QualityType.ph -> "수소이온농도"
            QualityType.rtmWqDoxn -> "용존산소량"
            QualityType.rtmWqTu -> "탁도"
            QualityType.rtmWqChpla -> "클로로필"
            QualityType.rtmWqSlnty -> "염분"
        }
    }
}
