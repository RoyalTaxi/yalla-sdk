package uz.yalla.foundation.location

import android.content.Context
import dev.icerock.moko.geo.LocationTracker
import dev.icerock.moko.permissions.PermissionsController
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual fun createLocationTracker(): LocationTracker =
    object : KoinComponent {
        val context: Context by inject()
    }.run {
        LocationTracker(
            permissionsController = PermissionsController(applicationContext = context)
        )
    }
