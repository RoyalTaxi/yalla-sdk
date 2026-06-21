package uz.yalla.maps.api.model

import uz.yalla.core.geo.GeoPoint

/** A one-shot interaction or lifecycle signal emitted by a [uz.yalla.maps.api.MapController]. */
public sealed class MapEvent {
    /** A map marker with the given [id] was tapped. */
    public data class MarkerTapped(
        val id: String
    ) : MapEvent()

    /** The map surface was tapped at [point]. */
    public data class MapTapped(
        val point: GeoPoint
    ) : MapEvent()

    /** The map surface was long-pressed at [point]. */
    public data class MapLongPressed(
        val point: GeoPoint
    ) : MapEvent()

    /**
     * A backend failed to become ready within the provider-ready timeout during a provider switch.
     * Emitted so a consumer can surface a degraded-map state instead of silently showing a blank map.
     */
    public data object ProviderUnavailable : MapEvent()
}
