package uz.yalla.maps.config.platform

import org.maplibre.compose.map.GestureOptions

expect fun getPlatformGestures(): GestureOptions

expect fun getDisabledGestures(): GestureOptions
