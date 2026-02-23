package uz.yalla.maps.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.currentComposer
import androidx.compose.ui.graphics.Color
import cocoapods.GoogleMaps.GMSCircle
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.UIKit.UIScreen
import uz.yalla.maps.model.LatLng

/**
 * iOS implementation of [Circle] that wraps GMSCircle.
 */
@OptIn(ExperimentalForeignApi::class)
actual class Circle(
    val gmsCircle: GMSCircle
) {
    actual val center: LatLng =
        gmsCircle.position.useContents {
            LatLng(latitude, longitude)
        }
    actual val radius: Double = gmsCircle.radius
}

@OptIn(ExperimentalForeignApi::class)
@Composable
@GoogleMapComposable
actual fun Circle(
    center: LatLng,
    radius: Double,
    fillColor: Color,
    strokeColor: Color,
    strokeWidth: Float,
) {
    val mapApplier =
        currentComposer.applier as? MapApplier
            ?: error("Circle must be used within a GoogleMap composable")

    ComposeNode<CircleNode, MapApplier>(
        factory = {
            val gmsCircle =
                GMSCircle
                    .circleWithPosition(
                        position = CLLocationCoordinate2DMake(center.latitude, center.longitude),
                        radius = radius
                    ).apply {
                        // iOS uses points, Android uses pixels; divide by scale for consistency
                        this.strokeWidth = strokeWidth.toDouble() / UIScreen.mainScreen.scale
                        this.strokeColor = strokeColor.toUIColor()
                        this.fillColor = fillColor.toUIColor()
                        this.map = mapApplier.mapView
                    }

            CircleNode(circle = gmsCircle)
        },
        update = {
            update(center) {
                this.circle.position = CLLocationCoordinate2DMake(it.latitude, it.longitude)
            }
            update(radius) { this.circle.radius = it }
            update(strokeWidth) {
                this.circle.strokeWidth = it.toDouble() / UIScreen.mainScreen.scale
            }
            update(strokeColor) { this.circle.strokeColor = it.toUIColor() }
            update(fillColor) { this.circle.fillColor = it.toUIColor() }
        }
    )
}
