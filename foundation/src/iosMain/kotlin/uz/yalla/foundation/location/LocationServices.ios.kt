package uz.yalla.foundation.location

import platform.CoreLocation.CLLocationManager
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString

/** @see isLocationServicesEnabled */
actual fun isLocationServicesEnabled(): Boolean =
    CLLocationManager.locationServicesEnabled()

/**
 * @see openLocationSettings
 *
 * iOS does not allow deep-linking to the system Location Services page, so this
 * opens the app's settings page via `UIApplicationOpenSettingsURLString`. Uses the
 * modern `open(url:options:completionHandler:)` API (iOS 10+).
 */
actual fun openLocationSettings() {
    val url = NSURL(string = UIApplicationOpenSettingsURLString)
    UIApplication.sharedApplication.openURL(
        url = url,
        options = emptyMap<Any?, Any>(),
        completionHandler = null,
    )
}
