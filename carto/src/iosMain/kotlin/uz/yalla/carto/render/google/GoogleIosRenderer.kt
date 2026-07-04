package uz.yalla.carto.render.google

import platform.UIKit.UIImage
import platform.UIKit.UIView
import uz.yalla.core.geo.GeoPoint

public data class GoogleIosCamera(
    val lat: Double,
    val lng: Double,
    val zoom: Float,
    val bearing: Float,
    val tilt: Float
)

public data class GoogleIosMarker(
    val id: String,
    val lat: Double,
    val lng: Double,
    val iconKey: String?,
    val anchorU: Float,
    val anchorV: Float,
    val rotation: Float,
    val flat: Boolean,
    val zIndex: Float
)

public data class GoogleIosPose(
    val id: String,
    val lat: Double,
    val lng: Double,
    val bearing: Float,
    val iconKey: String?,
    val anchorU: Float,
    val anchorV: Float
)

public data class GoogleIosRoute(
    val id: String,
    val points: List<GeoPoint>,
    val colorArgb: Int,
    val widthPt: Float,
    val dashed: Boolean,
    val zIndex: Float
)

public data class GoogleIosCircle(
    val id: String,
    val lat: Double,
    val lng: Double,
    val radiusMeters: Double,
    val fillArgb: Int,
    val strokeArgb: Int,
    val strokeWidthPt: Float,
    val zIndex: Float
)

public interface GoogleIosMapDelegate {
    public fun onReady()

    public fun onCameraMove(
        lat: Double,
        lng: Double,
        zoom: Float,
        bearing: Float,
        tilt: Float,
        isByUser: Boolean
    )

    public fun onCameraIdle(
        lat: Double,
        lng: Double,
        zoom: Float,
        bearing: Float,
        tilt: Float,
        isByUser: Boolean
    )

    public fun onMapTapped(
        lat: Double,
        lng: Double
    )

    public fun onMapLongPressed(
        lat: Double,
        lng: Double
    )
}

public interface GoogleIosMapRenderer {
    public val view: UIView

    public fun setDelegate(delegate: GoogleIosMapDelegate?)

    public fun setCamera(camera: GoogleIosCamera)

    public fun animateCamera(
        camera: GoogleIosCamera,
        durationMs: Int
    )

    public fun fitBounds(
        points: List<GeoPoint>,
        paddingPt: Double,
        maxZoom: Float,
        animate: Boolean
    )

    public fun setPadding(
        bottomPt: Double,
        durationMs: Int
    )

    public fun setAppearance(
        isDark: Boolean,
        styleJson: String?
    )

    public fun setIcons(icons: Map<String, UIImage>)

    public fun setMarkers(markers: List<GoogleIosMarker>)

    public fun applyPose(pose: GoogleIosPose)

    public fun setRoutes(routes: List<GoogleIosRoute>)

    public fun setCircles(circles: List<GoogleIosCircle>)

    public fun dispose()
}

public interface GoogleIosRendererFactory {
    public fun create(
        camera: GoogleIosCamera,
        minZoom: Float,
        maxZoom: Float
    ): GoogleIosMapRenderer
}

public object GoogleIosRendererHost {
    public var factory: GoogleIosRendererFactory? = null
}
