package uz.yalla.foundation.location

import platform.CoreLocation.CLLocationManager

/** @see isLocationServicesEnabled */
actual fun isLocationServicesEnabled(): Boolean =
    CLLocationManager.locationServicesEnabled()
