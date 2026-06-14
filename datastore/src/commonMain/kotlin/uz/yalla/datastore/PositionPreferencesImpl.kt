package uz.yalla.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import uz.yalla.core.geo.GeoPoint
import uz.yalla.core.preferences.PositionPreferences

internal class PositionPreferencesImpl(
    private val dataStore: DataStore<Preferences>,
    private val scope: CoroutineScope
) : PositionPreferences {
    override val lastMapPosition: Flow<GeoPoint> =
        dataStore.data.map {
            parseGeoPoint(it[PreferenceKeys.LAST_MAP_POSITION])
        }

    override fun setLastMapPosition(value: GeoPoint) {
        scope.launch {
            dataStore.edit { it[PreferenceKeys.LAST_MAP_POSITION] = "${value.lat},${value.lng}" }
        }
    }

    override val lastGpsPosition: Flow<GeoPoint> =
        dataStore.data.map { prefs ->
            parseGeoPoint(
                raw = prefs[PreferenceKeys.LAST_GPS_POSITION],
                fallbackRaw = prefs[PreferenceKeys.LAST_MAP_POSITION]
            )
        }

    override fun setLastGpsPosition(value: GeoPoint) {
        scope.launch {
            dataStore.edit { it[PreferenceKeys.LAST_GPS_POSITION] = "${value.lat},${value.lng}" }
        }
    }
}

internal fun parseGeoPoint(
    raw: String?,
    fallbackRaw: String? = null
): GeoPoint {
    val source = raw?.takeIf { it.isNotBlank() } ?: fallbackRaw.orEmpty()
    val parts = source.split(",", limit = 2)
    val lat = parts.getOrNull(0)?.toDoubleOrNull() ?: 0.0
    val lng = parts.getOrNull(1)?.toDoubleOrNull() ?: 0.0
    return GeoPoint(lat, lng)
}
