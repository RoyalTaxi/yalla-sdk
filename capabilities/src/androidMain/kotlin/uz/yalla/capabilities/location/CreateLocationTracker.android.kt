package uz.yalla.capabilities.location

import dev.icerock.moko.geo.LocationTracker
import dev.icerock.moko.permissions.PermissionsController
import uz.yalla.capabilities.capabilitiesContext

internal actual fun createLocationTracker(): LocationTracker =
    LocationTracker(
        permissionsController = PermissionsController(applicationContext = capabilitiesContext)
    )
