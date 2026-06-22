package uz.yalla.maps.api.model

import androidx.compose.runtime.Immutable

@Immutable
public sealed class MapStyle {
    public data class Url(
        val lightUrl: String,
        val darkUrl: String
    ) : MapStyle() {
        init {
            require(lightUrl.startsWith("https://")) { "MapStyle.Url.lightUrl must be https://" }
            require(darkUrl.startsWith("https://")) { "MapStyle.Url.darkUrl must be https://" }
        }
    }

    public data class InlineJson(
        val lightJson: String,
        val darkJson: String
    ) : MapStyle()

    public data object PlatformDefault : MapStyle()

    public companion object {
        public val CARTO: Url =
            Url(
                lightUrl = "https://basemaps.cartocdn.com/gl/positron-gl-style/style.json",
                darkUrl = "https://basemaps.cartocdn.com/gl/dark-matter-gl-style/style.json"
            )
    }
}
