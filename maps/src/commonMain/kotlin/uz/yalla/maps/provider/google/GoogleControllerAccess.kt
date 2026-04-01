package uz.yalla.maps.provider.google

import uz.yalla.maps.api.MapController
import uz.yalla.maps.provider.SwitchingMapController

/**
 * Extracts the [GoogleMapController] from this controller or its [SwitchingMapController] wrapper.
 *
 * @return The underlying [GoogleMapController].
 * @throws IllegalStateException if this is neither a [GoogleMapController] nor a [SwitchingMapController].
 * @since 0.0.1
 */
internal fun MapController.requireGoogleController(): GoogleMapController =
    when (this) {
        is GoogleMapController -> this
        is SwitchingMapController -> googleController
        else -> error("Google map requires GoogleMapController")
    }
