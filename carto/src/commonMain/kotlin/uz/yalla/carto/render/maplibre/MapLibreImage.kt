package uz.yalla.carto.render.maplibre

import org.maplibre.compose.style.BaseStyle
import org.maplibre.spatialk.geojson.Position
import uz.yalla.carto.state.MapStyle
import uz.yalla.core.geo.GeoPoint

internal object MapLibreKeys {
    const val ID = "id"
    const val ICON = "icon"
    const val ROTATION = "rotation"
    const val SORT = "sort"
}

internal fun GeoPoint.toPosition(): Position = Position(longitude = lng, latitude = lat)

internal fun Position.toGeoPoint(): GeoPoint = GeoPoint(lat = latitude, lng = longitude)

internal fun baseStyleFor(
    style: MapStyle,
    isDark: Boolean
): BaseStyle =
    when (style) {
        is MapStyle.Url -> BaseStyle.Uri(if (isDark) style.darkUrl else style.lightUrl)
        is MapStyle.InlineJson -> BaseStyle.Json(if (isDark) style.darkJson else style.lightJson)
        MapStyle.PlatformDefault -> BaseStyle.Demo
    }
