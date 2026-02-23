package uz.yalla.maps.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import uz.yalla.maps.model.Cap
import uz.yalla.maps.model.JointType
import uz.yalla.maps.model.LatLng

expect class Polyline {
    val points: List<LatLng>
}

@Composable
@GoogleMapComposable
expect fun Polyline(
    points: List<LatLng>,
    color: Color = Color.Black,
    endCap: Cap = Cap.Butt,
    jointType: JointType = JointType.Default,
    startCap: Cap = Cap.Butt,
    width: Float = 10f,
)
