package uz.yalla.maps.api.model

import androidx.compose.runtime.Immutable

@Immutable
sealed class MapStyle {
    data class Url(val lightUrl: String, val darkUrl: String) : MapStyle()

    data class InlineJson(val lightJson: String, val darkJson: String) : MapStyle()

    data object PlatformDefault : MapStyle()

    companion object {
        val CARTO = Url(
            lightUrl = "https://basemaps.cartocdn.com/gl/positron-gl-style/style.json",
            darkUrl = "https://basemaps.cartocdn.com/gl/dark-matter-gl-style/style.json"
        )
    }
}
