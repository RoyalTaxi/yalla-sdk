package uz.yalla.components.foundation.location

import dev.icerock.moko.geo.LocationTracker
import dev.icerock.moko.permissions.ios.PermissionsController

actual fun createLocationTracker(): LocationTracker =
    LocationTracker(
        permissionsController = PermissionsController()
    )
