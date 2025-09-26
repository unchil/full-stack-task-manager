package org.example.ktor

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NifsAndroid()
          //  NifsSeaWaterInfoDataGrid(modifier = Modifier.fillMaxWidth(0.9f).height(560.dp ).padding(20.dp))
            // NifsObservationBoxPlot()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    NifsAndroid()
}


@SuppressLint("UnrememberedMutableState")
@Composable
fun NifsAndroid(){

    MaterialTheme {
        val defaultCameraPosition =  CameraPosition.fromLatLngZoom( LatLng(37.5665,126.9780), 10f)
        val cameraPositionState =  CameraPositionState(position = defaultCameraPosition)

        Box(
            modifier = Modifier
                .fillMaxSize()

        ) {
            // Add GoogleMap here
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
            )
        }


    }
}