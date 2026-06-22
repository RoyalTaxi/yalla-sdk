package uz.yalla.capabilities.location

import dev.icerock.moko.geo.LocationTracker
import dev.icerock.moko.permissions.ios.PermissionsController

internal actual fun createLocationTracker(): LocationTracker =
    LocationTracker(
        permissionsController = PermissionsController()
    )
