package uz.yalla.maps.api.ios

import platform.UIKit.UIViewController
import uz.yalla.core.geo.GeoPoint
import uz.yalla.maps.api.model.MapCircle
import uz.yalla.maps.api.model.MapMarker
import uz.yalla.maps.api.model.MapRoute

interface IosMapRenderer {
    fun createViewController(): UIViewController

    fun setListener(listener: IosMapListener?)

    fun moveTo(target: GeoPoint, zoom: Float)

    fun animateTo(target: GeoPoint, zoom: Float, durationMs: Int)

    fun animateToWithBearing(target: GeoPoint, bearing: Float, zoom: Float, durationMs: Int)

    fun fitBounds(
        points: List<GeoPoint>,
        leftPt: Float,
        topPt: Float,
        rightPt: Float,
        bottomPt: Float,
        animate: Boolean
    )

    fun zoomIn()

    fun zoomOut()

    fun setZoom(zoom: Float)

    fun setStyleUrl(url: String)

    fun setStyleJson(json: String)

    fun setColorScheme(isDark: Boolean)

    fun setPaddingPt(leftPt: Float, topPt: Float, rightPt: Float, bottomPt: Float)

    fun setInteractionEnabled(enabled: Boolean)

    fun setMarkers(markers: List<MapMarker>)

    fun setRoutes(routes: List<MapRoute>)

    fun setCircles(circles: List<MapCircle>)

    fun setUserLocation(point: GeoPoint?)

    fun close()
}
