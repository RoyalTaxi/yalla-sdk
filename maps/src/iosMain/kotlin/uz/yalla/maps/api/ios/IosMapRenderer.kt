package uz.yalla.maps.api.ios

import platform.UIKit.UIViewController
import uz.yalla.core.geo.GeoPoint
import uz.yalla.maps.api.model.MapCircle
import uz.yalla.maps.api.model.MapMarker
import uz.yalla.maps.api.model.MapRoute

public interface IosMapRenderer {
    public fun createViewController(): UIViewController

    public fun setListener(listener: IosMapListener?)

    public fun moveTo(target: GeoPoint, zoom: Float)

    public fun animateTo(target: GeoPoint, zoom: Float, durationMs: Int)

    public fun animateToWithBearing(target: GeoPoint, bearing: Float, zoom: Float, durationMs: Int)

    public fun fitBounds(
        points: List<GeoPoint>,
        leftPt: Float,
        topPt: Float,
        rightPt: Float,
        bottomPt: Float,
        animate: Boolean
    )

    public fun zoomIn()

    public fun zoomOut()

    public fun setZoom(zoom: Float)

    public fun setStyleUrl(url: String)

    public fun setStyleJson(json: String)

    public fun setColorScheme(isDark: Boolean)

    public fun setPaddingPt(leftPt: Float, topPt: Float, rightPt: Float, bottomPt: Float)

    public fun setInteractionEnabled(enabled: Boolean)

    public fun setMarkers(markers: List<MapMarker>)

    public fun setRoutes(routes: List<MapRoute>)

    public fun setCircles(circles: List<MapCircle>)

    public fun setUserLocation(point: GeoPoint?)

    public fun close()
}
