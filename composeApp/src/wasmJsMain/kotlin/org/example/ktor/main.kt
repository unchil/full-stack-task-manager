package org.example.ktor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import org.example.ktor.theme.AppTheme


@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(document.body!!) {
        AppTheme {

            Column(
                modifier = Modifier.fillMaxSize()
                    .background(Color.White)
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
            ){

                Text("Nifs Sea Water Temperature Infomation",
                    modifier=Modifier.fillMaxWidth().align(Alignment.CenterHorizontally).padding(vertical = 20.dp),
                    fontSize=20.sp ,
                    fontWeight= FontWeight.Bold
                )

                NifsSeaWaterInfoDataGrid(modifier = Modifier.fillMaxWidth().height(600.dp ))
            }

        }
    }
}

