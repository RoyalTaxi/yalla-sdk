package uz.yalla.core.location

import kotlinx.coroutines.flow.StateFlow
import uz.yalla.core.geo.GeoPoint

public interface LocationProvider {
    public val currentLocation: StateFlow<GeoPoint?>

    public val currentAccuracy: StateFlow<Double?>

    public fun startTracking()

    public fun stopTracking()
}
