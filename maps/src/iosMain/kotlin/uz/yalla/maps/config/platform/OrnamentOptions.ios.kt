package uz.yalla.maps.config.platform

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.Alignment
import org.maplibre.compose.map.OrnamentOptions

actual fun getPlatformOrnamentOptions(padding: PaddingValues): OrnamentOptions =
    OrnamentOptions(
        padding = padding,
        isLogoEnabled = false,
        logoAlignment = Alignment.BottomStart,
        isAttributionEnabled = false,
        isCompassEnabled = false,
        isScaleBarEnabled = false
    )
