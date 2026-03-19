package uz.yalla.maps.model

/**
 * Type of map tiles rendered by the map view.
 *
 * Maps to platform constants on Android (`com.google.android.gms.maps.GoogleMap.MAP_TYPE_*`)
 * and iOS (`kGMSType*`).
 *
 * @since 0.0.1
 */
enum class MapType {
    /** No base map tiles. */
    NONE,

    /** Standard road map. */
    NORMAL,

    /** Satellite imagery without labels. */
    SATELLITE,

    /** Satellite imagery with road and label overlay. */
    HYBRID,

    /** Topographic terrain map. */
    TERRAIN
}
