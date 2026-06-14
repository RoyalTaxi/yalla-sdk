package uz.yalla.maps.api.ios

import uz.yalla.core.geo.GeoPoint

public interface IosMapListener {
    public fun onCameraMove(target: GeoPoint, zoom: Float, bearing: Float, tilt: Float, isByUser: Boolean)

    public fun onCameraIdle(target: GeoPoint, zoom: Float, bearing: Float, tilt: Float, isByUser: Boolean)

    public fun onReady()

    public fun onMarkerTapped(id: String)

    public fun onMapTapped(point: GeoPoint)

    public fun onMapLongPressed(point: GeoPoint)
}
