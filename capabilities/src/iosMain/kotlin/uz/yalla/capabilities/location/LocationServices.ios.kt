package uz.yalla.capabilities.location

import platform.CoreLocation.CLLocationManager
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString

public actual fun isLocationServicesEnabled(): Boolean = CLLocationManager.locationServicesEnabled()

public actual fun openLocationSettings() {
    val url = NSURL(string = UIApplicationOpenSettingsURLString)
    UIApplication.sharedApplication.openURL(
        url = url,
        options = emptyMap<Any?, Any>(),
        completionHandler = null
    )
}
