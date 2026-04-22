package uz.yalla.foundation.location

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import org.koin.core.context.GlobalContext

private fun requireKoinContext(fnName: String): Context =
    runCatching { GlobalContext.get().get<Context>() }.getOrElse { cause ->
        error(
            "yalla-sdk foundation.location.$fnName requires a Koin global Context binding. " +
                "Call startKoin { androidContext(applicationContext) } before invoking this function. " +
                "Root cause: ${cause::class.simpleName}: ${cause.message}",
        )
    }

/** @see isLocationServicesEnabled */
actual fun isLocationServicesEnabled(): Boolean {
    val context = requireKoinContext("isLocationServicesEnabled")
    val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
        lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
}

/** @see openLocationSettings */
actual fun openLocationSettings() {
    val context = requireKoinContext("openLocationSettings")
    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}
