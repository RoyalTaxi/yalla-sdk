package uz.yalla.capabilities.location

/** Returns `true` if device location services (GPS) are currently enabled. */
public expect fun isLocationServicesEnabled(): Boolean

/** Opens the system location-settings screen so the user can enable location services. */
public expect fun openLocationSettings()
