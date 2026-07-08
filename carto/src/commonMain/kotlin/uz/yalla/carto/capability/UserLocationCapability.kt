package uz.yalla.carto.capability

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import uz.yalla.carto.CartoBuilder
import uz.yalla.carto.CartoMap
import uz.yalla.carto.capability.framework.Capability
import uz.yalla.carto.capability.framework.CapabilityContext
import uz.yalla.carto.capability.framework.CapabilityHandle
import uz.yalla.carto.capability.framework.CapabilityKey
import uz.yalla.carto.capability.source.CircleSource
import uz.yalla.carto.capability.source.MarkerSource
import uz.yalla.carto.model.CartoCircle
import uz.yalla.carto.model.CartoMarker
import uz.yalla.carto.model.MapAnchor
import uz.yalla.carto.model.ZBand
import uz.yalla.core.geo.GeoPoint

public data class UserLocationConfig(
    val dotIconKey: String = USER_LOCATION_DOT_ICON_KEY,
    val ringFillArgb: Int = 0x33562DF8,
    val ringStrokeArgb: Int = 0x66562DF8,
    val minRingAccuracyMeters: Double = 20.0,
    val enabled: Boolean = true
)

public class UserLocationHandle internal constructor(
    private val config: UserLocationConfig
) : CapabilityHandle,
    MarkerSource,
    CircleSource {
    private var point: GeoPoint? = null
    private var accuracyMeters: Double? = null
    private var enabled: Boolean = config.enabled

    private val mutableMarkers = MutableStateFlow<List<CartoMarker>>(emptyList())
    override val markers: StateFlow<List<CartoMarker>> = mutableMarkers.asStateFlow()

    private val mutableCircles = MutableStateFlow<List<CartoCircle>>(emptyList())
    override val circles: StateFlow<List<CartoCircle>> = mutableCircles.asStateFlow()

    public fun setPoint(next: GeoPoint?) {
        point = next?.takeIf { it != GeoPoint.Zero }
        publish()
    }

    public fun setAccuracy(next: Double?) {
        accuracyMeters = next?.takeIf { it > 0 }
        publish()
    }

    public fun setEnabled(next: Boolean) {
        enabled = next
        publish()
    }

    private fun publish() {
        val visible = point.takeIf { enabled }
        if (visible == null) {
            mutableMarkers.value = emptyList()
            mutableCircles.value = emptyList()
            return
        }
        mutableMarkers.value =
            listOf(
                CartoMarker(
                    id = DOT_ID,
                    point = visible,
                    iconKey = config.dotIconKey,
                    anchor = MapAnchor.CENTER,
                    zBand = ZBand.USER_LOCATION
                )
            )
        val accuracy = accuracyMeters
        mutableCircles.value =
            if (accuracy != null && accuracy > config.minRingAccuracyMeters) {
                listOf(
                    CartoCircle(
                        id = RING_ID,
                        center = visible,
                        radiusMeters = accuracy,
                        fillArgb = config.ringFillArgb,
                        strokeArgb = config.ringStrokeArgb,
                        strokeWidthDp = 1f,
                        zBand = ZBand.USER_LOCATION
                    )
                )
            } else {
                emptyList()
            }
    }

    private companion object {
        const val RING_ID = "yalla-user-location-ring"
        const val DOT_ID = "yalla-user-location-dot"
    }
}

public class UserLocationCapability(
    private val config: UserLocationConfig = UserLocationConfig()
) : Capability<UserLocationHandle> {
    override val key: CapabilityKey<UserLocationHandle> get() = Key

    override fun create(context: CapabilityContext): UserLocationHandle = UserLocationHandle(config)

    public companion object {
        public val Key: CapabilityKey<UserLocationHandle> = CapabilityKey("user-location")
    }
}

public fun CartoBuilder.userLocation(config: UserLocationConfig = UserLocationConfig()) {
    add(UserLocationCapability(config))
}

public val CartoMap.userLocation: UserLocationHandle get() = capability(UserLocationCapability.Key)
