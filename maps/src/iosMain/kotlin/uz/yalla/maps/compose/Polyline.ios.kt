package uz.yalla.maps.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.currentComposer
import androidx.compose.ui.graphics.Color
import cocoapods.GoogleMaps.GMSMutablePath
import cocoapods.GoogleMaps.GMSPolyline
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.UIKit.UIScreen
import uz.yalla.maps.model.Cap
import uz.yalla.maps.model.JointType
import uz.yalla.maps.model.LatLng

@OptIn(ExperimentalForeignApi::class)
actual class Polyline(
    val gmsPolyline: GMSPolyline
) {
    actual val points: List<LatLng> =
        gmsPolyline.path?.let { path ->
            (0 until path.count().toInt()).map { index ->
                path.coordinateAtIndex(index.toULong()).useContents { LatLng(latitude, longitude) }
            }
        } ?: emptyList()
}

@OptIn(ExperimentalForeignApi::class)
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
    val mapApplier = currentComposer.applier as? MapApplier ?: return

    ComposeNode<PolylineNode, MapApplier>(
        factory = {
            val path =
                GMSMutablePath().apply {
                    points.forEach { addCoordinate(CLLocationCoordinate2DMake(it.latitude, it.longitude)) }
                }
            val polyline =
                GMSPolyline().apply {
                    this.path = path
                    strokeWidth = width.toDouble() / UIScreen.mainScreen.scale
                    strokeColor = color.toUIColor()
                    map = mapApplier.mapView
                }
            PolylineNode(polyline)
        },
        update = {
            update(points) {
                polyline.path =
                    GMSMutablePath().apply {
                        points.forEach { addCoordinate(CLLocationCoordinate2DMake(it.latitude, it.longitude)) }
                    }
            }
            update(width) { polyline.strokeWidth = it.toDouble() / UIScreen.mainScreen.scale }
            update(color) { polyline.strokeColor = it.toUIColor() }
        }
    )
}
