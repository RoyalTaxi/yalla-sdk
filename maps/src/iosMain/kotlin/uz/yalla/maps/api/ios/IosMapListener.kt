package uz.yalla.maps.api.ios

import uz.yalla.core.geo.GeoPoint

interface IosMapListener {
    fun onCameraMove(target: GeoPoint, zoom: Float, bearing: Float, tilt: Float, isByUser: Boolean)

    fun onCameraIdle(target: GeoPoint, zoom: Float, bearing: Float, tilt: Float, isByUser: Boolean)

    fun onReady()

    fun onMarkerTapped(id: String)

    fun onMapTapped(point: GeoPoint)

    fun onMapLongPressed(point: GeoPoint)
}
