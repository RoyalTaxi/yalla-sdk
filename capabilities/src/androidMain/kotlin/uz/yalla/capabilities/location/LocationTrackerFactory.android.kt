package uz.yalla.capabilities.location

import android.content.Context
import dev.icerock.moko.geo.LocationTracker
import dev.icerock.moko.permissions.PermissionsController
import org.koin.core.context.GlobalContext

public actual fun createLocationTracker(): LocationTracker {
    val context: Context = GlobalContext.get().get()
    return LocationTracker(
        permissionsController = PermissionsController(applicationContext = context)
    )
}
