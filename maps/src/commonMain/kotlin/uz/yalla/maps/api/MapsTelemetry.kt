package uz.yalla.maps.api

import uz.yalla.maps.api.model.MapMarkerIcon

interface MapsTelemetry {
    fun onMapReadyTimeout(provider: String) {}

    fun onIconLoadFailed(icon: MapMarkerIcon, cause: Throwable) {}

    fun onStyleLoadFailed(uri: String, cause: Throwable) {}

    fun onCameraOpsCancelled(reason: String) {}

    fun onUnsupportedFeature(feature: String, provider: String) {}
}
