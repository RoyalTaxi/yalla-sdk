package uz.yalla.foundation.location

import platform.CoreLocation.CLLocationManager
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString

/** @see isLocationServicesEnabled */
actual fun isLocationServicesEnabled(): Boolean =
    CLLocationManager.locationServicesEnabled()

/** @see openLocationSettings */
actual fun openLocationSettings() {
    val url = NSURL(string = UIApplicationOpenSettingsURLString) ?: return
    UIApplication.sharedApplication.openURL(url)
}
