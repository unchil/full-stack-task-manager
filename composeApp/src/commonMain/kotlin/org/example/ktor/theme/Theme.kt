package org.example.ktor.theme


import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import full_stack_task_manager.composeapp.generated.resources.AppleGothic
import full_stack_task_manager.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.Font


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



    val typography = Typography(
        displayLarge = Typography().displayLarge.copy(fontFamily = AppleGothic),
        displayMedium = Typography().displayMedium.copy(fontFamily = AppleGothic),
        displaySmall = Typography().displaySmall.copy(fontFamily = AppleGothic),
        headlineLarge = Typography().headlineLarge.copy(fontFamily = AppleGothic),
        headlineMedium = Typography().headlineMedium.copy(fontFamily = AppleGothic),
        headlineSmall = Typography().headlineSmall.copy(fontFamily = AppleGothic),
        titleLarge = Typography().titleLarge.copy(fontFamily = AppleGothic),
        titleMedium = Typography().titleMedium.copy(fontFamily = AppleGothic),
        titleSmall = Typography().titleSmall.copy(fontFamily = AppleGothic),
        bodyLarge = Typography().bodyLarge.copy(fontFamily = AppleGothic),
        bodyMedium = Typography().bodyMedium.copy(fontFamily = AppleGothic),
        bodySmall = Typography().bodySmall.copy(fontFamily = AppleGothic),
        labelLarge = Typography().labelLarge.copy(fontFamily = AppleGothic),
        labelMedium = Typography().labelMedium.copy(fontFamily = AppleGothic),
        labelSmall = Typography().labelSmall.copy(fontFamily = AppleGothic)
    )



    MaterialTheme(typography = typography) {
        content()
    }

}