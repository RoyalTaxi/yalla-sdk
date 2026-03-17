package uz.yalla.foundation.location

import dev.icerock.moko.geo.LocationTracker
import dev.icerock.moko.permissions.ios.PermissionsController

/** @see createLocationTracker */
actual fun createLocationTracker(): LocationTracker =
    LocationTracker(
        permissionsController = PermissionsController()
    )
