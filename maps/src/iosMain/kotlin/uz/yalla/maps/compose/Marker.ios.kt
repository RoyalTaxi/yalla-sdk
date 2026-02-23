package uz.yalla.maps.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.currentComposer
import androidx.compose.ui.geometry.Offset
import cocoapods.GoogleMaps.GMSMarker
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGPointMake
import platform.CoreLocation.CLLocationCoordinate2DMake

@OptIn(ExperimentalForeignApi::class)
@Composable
@GoogleMapComposable
actual fun Marker(
    state: MarkerState,
    icon: BitmapDescriptor?,
    anchor: Offset,
    flat: Boolean,
    rotation: Float,
) {
    val mapApplier =
        currentComposer.applier as? MapApplier
            ?: error("Marker must be used within a GoogleMap composable")

    ComposeNode<MarkerNode, MapApplier>(
        factory = {
            val gmsMarker =
                GMSMarker().apply {
                    position =
                        CLLocationCoordinate2DMake(
                            state.position.latitude,
                            state.position.longitude
                        )
                    this.groundAnchor = CGPointMake(anchor.x.toDouble(), anchor.y.toDouble())
                    this.flat = flat
                    this.icon = icon?.uiImage
                    this.rotation = rotation.toDouble()
                    this.map = mapApplier.mapView
                }

            MarkerNode(marker = gmsMarker)
        },
        update = {
            update(state.position) {
                this.marker.position = CLLocationCoordinate2DMake(it.latitude, it.longitude)
            }
            update(anchor) {
                this.marker.groundAnchor = CGPointMake(it.x.toDouble(), it.y.toDouble())
            }
            update(flat) { this.marker.flat = it }
            update(icon) { this.marker.icon = it?.uiImage }
            update(rotation) { this.marker.rotation = it.toDouble() }
        }
    )
}
