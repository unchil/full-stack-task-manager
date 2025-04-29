package org.example.ktor.theme


import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import full_stack_task_manager.composeapp.generated.resources.AppleGothic
import full_stack_task_manager.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.Font
import full_stack_task_manager.composeapp.generated.resources.AppleSDGothicNeo

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

    val AppleSDGothicNeo = FontFamily(
        Font(Res.font.AppleSDGothicNeo, FontWeight.Bold),
        Font(Res.font.AppleSDGothicNeo, FontWeight.SemiBold),
        Font(Res.font.AppleSDGothicNeo, FontWeight.ExtraBold),
        Font(Res.font.AppleSDGothicNeo, FontWeight.Medium),
        Font(Res.font.AppleSDGothicNeo, FontWeight.Normal),
        Font(Res.font.AppleSDGothicNeo, FontWeight.Black),
        Font(Res.font.AppleSDGothicNeo, FontWeight.Thin),
        Font(Res.font.AppleSDGothicNeo, FontWeight.ExtraLight),
        Font(Res.font.AppleSDGothicNeo, FontWeight.Light)
    )




    val typography = Typography(
        displayLarge = Typography().displayLarge.copy(fontFamily = AppleSDGothicNeo),
        displayMedium = Typography().displayMedium.copy(fontFamily = AppleSDGothicNeo),
        displaySmall = Typography().displaySmall.copy(fontFamily = AppleSDGothicNeo),
        headlineLarge = Typography().headlineLarge.copy(fontFamily = AppleSDGothicNeo),
        headlineMedium = Typography().headlineMedium.copy(fontFamily = AppleSDGothicNeo),
        headlineSmall = Typography().headlineSmall.copy(fontFamily = AppleSDGothicNeo),
        titleLarge = Typography().titleLarge.copy(fontFamily = AppleSDGothicNeo),
        titleMedium = Typography().titleMedium.copy(fontFamily = AppleSDGothicNeo),
        titleSmall = Typography().titleSmall.copy(fontFamily = AppleSDGothicNeo),
        bodyLarge = Typography().bodyLarge.copy(fontFamily = AppleSDGothicNeo),
        bodyMedium = Typography().bodyMedium.copy(fontFamily = AppleSDGothicNeo),
        bodySmall = Typography().bodySmall.copy(fontFamily = AppleSDGothicNeo),
        labelLarge = Typography().labelLarge.copy(fontFamily = AppleSDGothicNeo),
        labelMedium = Typography().labelMedium.copy(fontFamily = AppleSDGothicNeo),
        labelSmall = Typography().labelSmall.copy(fontFamily = AppleSDGothicNeo)
    )



    MaterialTheme(typography = typography) {
        content()
    }

}