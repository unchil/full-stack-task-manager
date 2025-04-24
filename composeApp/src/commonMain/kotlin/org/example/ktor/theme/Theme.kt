package org.example.ktor.theme


import androidx.compose.runtime.Composable

import full_stack_task_manager.composeapp.generated.resources.AppleGothic
import full_stack_task_manager.composeapp.generated.resources.Res

//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.MaterialTheme
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Typography

import androidx.compose.ui.text.TextStyle
import org.jetbrains.compose.resources.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp



@Composable
fun AppTheme(content: @Composable () -> Unit) {

    val AppleGothic = FontFamily(
        Font(Res.font.AppleGothic, FontWeight.Bold),
        Font(Res.font.AppleGothic, FontWeight.SemiBold),
        Font(Res.font.AppleGothic, FontWeight.ExtraBold),
        Font(Res.font.AppleGothic, FontWeight.Medium),
        Font(Res.font.AppleGothic, FontWeight.Normal),
        Font(Res.font.AppleGothic, FontWeight.Black),
        Font(Res.font.AppleGothic, FontWeight.Thin),
        Font(Res.font.AppleGothic, FontWeight.ExtraLight),
        Font(Res.font.AppleGothic, FontWeight.Light)
    )

    val AppTypography = Typography(

        defaultFontFamily = AppleGothic,
        body1 = TextStyle(
            fontSize = 15.sp,
        )

    )

    MaterialTheme(typography = AppTypography) {
        content()
    }

}