package uz.yalla.maps.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import uz.yalla.maps.model.LatLng
import com.google.android.gms.maps.model.Circle as GoogleCircle
import com.google.maps.android.compose.Circle as AndroidCircle

/**
 * Android implementation of [Circle] wrapping a Google Maps SDK `GMSCircle`.
 *
 * @property googleCircle The underlying Google Maps circle overlay.
 * @since 0.0.1
 */
actual class Circle(
    val googleCircle: GoogleCircle
) {
    actual val center: LatLng = LatLng(googleCircle.center.latitude, googleCircle.center.longitude)
    actual val radius: Double = googleCircle.radius
}

@Composable
@GoogleMapComposable
actual fun Circle(
    center: LatLng,
    radius: Double,
    fillColor: Color,
    strokeColor: Color,
    strokeWidth: Float,
) {
    AndroidCircle(
        center = center.toGoogleLatLng(),
        radius = radius,
        fillColor = fillColor,
        strokeColor = strokeColor,
        strokeWidth = strokeWidth,
    )
}
