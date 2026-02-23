package uz.yalla.maps.config.platform

import org.maplibre.compose.map.GestureOptions

actual fun getPlatformGestures(): GestureOptions =
    GestureOptions(
        isRotateEnabled = false,
        isScrollEnabled = true,
        isTiltEnabled = false,
        isZoomEnabled = true,
        isHapticFeedbackEnabled = true
    )

actual fun getDisabledGestures(): GestureOptions =
    GestureOptions(
        isScrollEnabled = false,
        isZoomEnabled = false,
        isRotateEnabled = false,
        isTiltEnabled = false,
        isHapticFeedbackEnabled = false
    )
