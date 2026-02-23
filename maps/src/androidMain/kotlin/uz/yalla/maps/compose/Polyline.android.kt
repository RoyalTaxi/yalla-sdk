package uz.yalla.maps.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import uz.yalla.maps.model.Cap
import uz.yalla.maps.model.JointType
import uz.yalla.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline as GooglePolyline
import com.google.maps.android.compose.Polyline as AndroidPolyline

actual class Polyline(
    val googlePolyline: GooglePolyline
) {
    actual val points: List<LatLng> = googlePolyline.points.map { LatLng(it.latitude, it.longitude) }
}

@Composable
@GoogleMapComposable
actual fun Polyline(
    points: List<LatLng>,
    color: Color,
    endCap: Cap,
    jointType: JointType,
    startCap: Cap,
    width: Float,
) {
    val googlePoints = remember(points) { points.map(LatLng::toGoogleLatLng) }

    AndroidPolyline(
        points = googlePoints,
        color = color,
        endCap = endCap.toGoogleCap(),
        jointType = jointType.toGoogleJointType(),
        startCap = startCap.toGoogleCap(),
        width = width,
    )
}
