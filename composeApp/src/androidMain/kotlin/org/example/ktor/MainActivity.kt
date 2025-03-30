package org.example.ktor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NifsAndroid()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
   // NifsAndroid()
}


@Composable
fun NifsAndroid(){

    MaterialTheme {

        val home = LatLng(37.3860, 126.9344)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(home, 18f)
        }
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            Marker(
                state = MarkerState(position = home),
                title = "Home",
                snippet = "Home Sweet Home"
            )
        }


    }
}