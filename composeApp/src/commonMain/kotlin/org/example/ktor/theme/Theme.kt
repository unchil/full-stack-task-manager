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
        Font(Res.font.AppleGothic, FontWeight.Thin)

    )

    val AppTypography = Typography(
        h1 = TextStyle(
            fontFamily = AppleGothic,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        ),
        h2 = TextStyle(
            fontFamily = AppleGothic,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        ),
        h3 = TextStyle(
            fontFamily = AppleGothic,
        ),
        h4 = TextStyle(
            fontFamily = AppleGothic,
        ),
        h5 = TextStyle(
            fontFamily = AppleGothic,
        ),
        h6 = TextStyle(
            fontFamily = AppleGothic,
        ),
        subtitle1 = TextStyle(
            fontFamily = AppleGothic,
        ),
        subtitle2 = TextStyle(
            fontFamily = AppleGothic,
        ),
        body1 = TextStyle(
            fontFamily = AppleGothic,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp
        ),
        body2 = TextStyle(
            fontFamily = AppleGothic,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp
        ),
        button = TextStyle(
            fontFamily = AppleGothic,
        ),
        caption = TextStyle(
            fontFamily = AppleGothic,
        ),
        overline = TextStyle(
            fontFamily = AppleGothic,
        ),
    )

    MaterialTheme(typography = AppTypography) {
        content()
    }

}