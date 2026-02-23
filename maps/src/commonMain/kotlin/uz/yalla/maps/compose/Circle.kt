package uz.yalla.maps.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import uz.yalla.maps.model.LatLng

expect class Circle {
    val center: LatLng
    val radius: Double
}

@Composable
@GoogleMapComposable
expect fun Circle(
    center: LatLng,
    radius: Double = 0.0,
    fillColor: Color = Color.Transparent,
    strokeColor: Color = Color.Black,
    strokeWidth: Float = 10f,
)
