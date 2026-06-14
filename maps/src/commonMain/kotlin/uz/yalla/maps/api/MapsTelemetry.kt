package uz.yalla.maps.api

import uz.yalla.maps.api.model.MapMarkerIcon

public interface MapsTelemetry {
    public fun onMapReadyTimeout(provider: String) {}

    public fun onIconLoadFailed(
        icon: MapMarkerIcon,
        cause: Throwable
    ) {}

    public fun onStyleLoadFailed(
        uri: String,
        cause: Throwable
    ) {}

    public fun onCameraOpsCancelled(reason: String) {}

    public fun onUnsupportedFeature(
        feature: String,
        provider: String
    ) {}
}
