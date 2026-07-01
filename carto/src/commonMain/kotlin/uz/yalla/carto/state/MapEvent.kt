package uz.yalla.carto.state

import uz.yalla.carto.camera.CameraView
import uz.yalla.core.geo.GeoPoint

public sealed interface MapEvent {
    public data class CameraMoved(
        val view: CameraView,
        val isByUser: Boolean
    ) : MapEvent

    public data class CameraIdle(
        val view: CameraView,
        val isByUser: Boolean
    ) : MapEvent

    public data class MapTapped(
        val point: GeoPoint
    ) : MapEvent

    public data class MapLongPressed(
        val point: GeoPoint
    ) : MapEvent
}
