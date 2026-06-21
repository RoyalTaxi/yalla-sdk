package uz.yalla.capabilities.location

import androidx.activity.ComponentActivity

public fun DeviceLocationProvider.bindToActivity(activity: ComponentActivity) {
    locationTracker.bind(activity)
}
