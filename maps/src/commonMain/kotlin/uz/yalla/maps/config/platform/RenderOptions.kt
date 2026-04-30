package uz.yalla.maps.config.platform

import org.maplibre.compose.map.RenderOptions

/**
 * Returns the default MapLibre render configuration for the current platform.
 *
 * On Android, selects `SurfaceView` for optimal rendering performance.
 * On iOS, uses the default Metal-backed renderer.
 *
 * @return Platform-specific [RenderOptions].
 */
expect fun getPlatformRenderOptions(): RenderOptions
