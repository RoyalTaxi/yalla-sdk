package uz.yalla.capabilities.location

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import org.koin.core.context.GlobalContext

private val appContext: Context get() = GlobalContext.get().get()

public actual fun isLocationServicesEnabled(): Boolean {
    val manager = appContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return manager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
        manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
}

public actual fun openLocationSettings() {
    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    appContext.startActivity(intent)
}
