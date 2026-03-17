package uz.yalla.foundation.location

import android.content.Context
import dev.icerock.moko.geo.LocationTracker
import dev.icerock.moko.permissions.PermissionsController
import org.koin.core.context.GlobalContext

actual fun createLocationTracker(): LocationTracker {
    val context: Context = GlobalContext.get().get()
    return LocationTracker(
        permissionsController = PermissionsController(applicationContext = context)
    )
}
