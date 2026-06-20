package uz.yalla.maps.config

import uz.yalla.maps.api.MapController

/** Creates the per-backend [MapController]s that [uz.yalla.maps.api.SwitchingMapController] swaps between. */
public interface MapFactory {
    /** Creates a fresh Google Maps-backed controller. */
    public fun createGoogleController(): MapController

    /** Creates a fresh MapLibre-backed controller. */
    public fun createLibreController(): MapController
}
