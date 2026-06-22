package uz.yalla.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import uz.yalla.core.geo.GeoPoint
import uz.yalla.core.preferences.PositionPreferences

internal class PositionPreferencesImpl(
    private val dataStore: DataStore<Preferences>,
    private val scope: CoroutineScope
) : PositionPreferences {
    override val lastMapPosition: Flow<GeoPoint> =
        dataStore.readFlow {
            parseGeoPoint(it[PreferenceKeys.LAST_MAP_POSITION])
        }

    override fun setLastMapPosition(value: GeoPoint) {
        dataStore.write(scope) { it[PreferenceKeys.LAST_MAP_POSITION] = "${value.lat},${value.lng}" }
    }

    override val lastGpsPosition: Flow<GeoPoint> =
        dataStore.readFlow { prefs ->
            parseGeoPoint(
                raw = prefs[PreferenceKeys.LAST_GPS_POSITION],
                fallbackRaw = prefs[PreferenceKeys.LAST_MAP_POSITION]
            )
        }

    override fun setLastGpsPosition(value: GeoPoint) {
        dataStore.write(scope) { it[PreferenceKeys.LAST_GPS_POSITION] = "${value.lat},${value.lng}" }
    }
}

internal fun parseGeoPoint(
    raw: String?,
    fallbackRaw: String? = null
): GeoPoint = decodeGeoPoint(raw) ?: decodeGeoPoint(fallbackRaw) ?: GeoPoint.Zero

private fun decodeGeoPoint(raw: String?): GeoPoint? {
    val source = raw?.takeIf { it.isNotBlank() } ?: return null
    val parts = source.split(",", limit = 2)
    val lat = parts.getOrNull(0)?.toDoubleOrNull() ?: return null
    val lng = parts.getOrNull(1)?.toDoubleOrNull() ?: return null
    return GeoPoint(lat, lng)
}
