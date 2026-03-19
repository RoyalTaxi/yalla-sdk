package uz.yalla.maps.config.platform

import androidx.compose.foundation.layout.PaddingValues
import org.maplibre.compose.map.OrnamentOptions

/**
 * Returns the default MapLibre ornament configuration for the current platform.
 *
 * All ornaments (logo, attribution, compass, scale bar) are disabled by default
 * since the Yalla UI provides its own controls.
 *
 * @param padding Optional padding applied to ornament positioning.
 * @return Platform-specific [OrnamentOptions].
 * @since 0.0.1
 */
expect fun getPlatformOrnamentOptions(padding: PaddingValues = PaddingValues()): OrnamentOptions
