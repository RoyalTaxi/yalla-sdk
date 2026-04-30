package uz.yalla.maps.config.platform

import org.maplibre.compose.map.GestureOptions

/**
 * Returns the default MapLibre gesture configuration for the current platform.
 *
 * @return Platform-specific [GestureOptions] with standard scroll and zoom enabled.
 */
expect fun getPlatformGestures(): GestureOptions

/**
 * Returns a MapLibre gesture configuration with all gestures disabled.
 *
 * Used for static/non-interactive map views.
 *
 * @return Platform-specific [GestureOptions] with all interactions disabled.
 */
expect fun getDisabledGestures(): GestureOptions
