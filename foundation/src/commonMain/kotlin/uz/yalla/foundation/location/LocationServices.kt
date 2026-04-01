package uz.yalla.foundation.location

/**
 * Checks whether device-level location services (GPS/network) are enabled.
 *
 * This is independent of app-level permission — a user may grant location
 * permission but have GPS toggled off in device settings.
 *
 * - **Android:** Checks [GPS_PROVIDER] or [NETWORK_PROVIDER] via [android.location.LocationManager].
 * - **iOS:** Calls [CLLocationManager.locationServicesEnabled].
 *
 * @return `true` if at least one location provider is active
 * @since 0.0.8
 */
expect fun isLocationServicesEnabled(): Boolean
