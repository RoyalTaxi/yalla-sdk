package uz.yalla.maps.config.platform

import org.maplibre.compose.map.GestureOptions

actual fun getPlatformGestures(): GestureOptions =
    GestureOptions(
        isScrollEnabled = true,
        isZoomEnabled = true,
        isQuickZoomEnabled = true,
        isRotateEnabled = false,
        isTiltEnabled = false,
        isDoubleTapEnabled = true
    )

actual fun getDisabledGestures(): GestureOptions =
    GestureOptions(
        isScrollEnabled = false,
        isZoomEnabled = false,
        isQuickZoomEnabled = false,
        isRotateEnabled = false,
        isTiltEnabled = false,
        isDoubleTapEnabled = false
    )
