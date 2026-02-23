package uz.yalla.maps.api.model

import androidx.compose.runtime.Immutable

@Immutable
data class MapStyle(
    val lightStyleUrl: String,
    val darkStyleUrl: String
) {
    companion object {
        val CARTO =
            MapStyle(
                lightStyleUrl = "https://basemaps.cartocdn.com/gl/positron-gl-style/style.json",
                darkStyleUrl = "https://basemaps.cartocdn.com/gl/dark-matter-gl-style/style.json"
            )

        val GOOGLE =
            MapStyle(
                lightStyleUrl = "",
                darkStyleUrl = ""
            )
    }
}
