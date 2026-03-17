package uz.yalla.foundation.location

import dev.icerock.moko.geo.LocationTracker

/**
 * Creates a platform-specific [LocationTracker].
 *
 * - **Android:** Uses Koin-injected application Context to create PermissionsController.
 * - **iOS:** Creates PermissionsController directly (no DI needed).
 *
 * @return Configured [LocationTracker] for the current platform
 * @since 0.0.1
 */
expect fun createLocationTracker(): LocationTracker
