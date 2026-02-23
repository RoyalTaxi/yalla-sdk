package uz.yalla.maps.provider.google

import uz.yalla.maps.api.MapController
import uz.yalla.maps.provider.SwitchingMapController

internal fun MapController.requireGoogleController(): GoogleMapController =
    when (this) {
        is GoogleMapController -> this
        is SwitchingMapController -> googleController
        else -> error("Google map requires GoogleMapController")
    }
