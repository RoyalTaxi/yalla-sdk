package uz.yalla.foundation.location

import android.content.Context
import android.location.LocationManager
import org.koin.core.context.GlobalContext

/** @see isLocationServicesEnabled */
actual fun isLocationServicesEnabled(): Boolean {
    val context: Context = GlobalContext.get().get()
    val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
        lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
}
