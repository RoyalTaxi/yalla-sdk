package uz.yalla.maps.config.platform

import androidx.compose.foundation.layout.PaddingValues
import org.maplibre.compose.map.OrnamentOptions

expect fun getPlatformOrnamentOptions(padding: PaddingValues = PaddingValues()): OrnamentOptions
