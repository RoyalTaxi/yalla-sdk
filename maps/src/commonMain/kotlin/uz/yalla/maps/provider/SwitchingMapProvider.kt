package uz.yalla.maps.provider

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.yalla.core.geo.GeoPoint
import uz.yalla.core.settings.MapKind
import uz.yalla.maps.api.ExtendedMap
import uz.yalla.maps.api.LiteMap
import uz.yalla.maps.api.MapController
import uz.yalla.maps.api.MapProvider
import uz.yalla.maps.api.MapScope
import uz.yalla.maps.api.StaticMap
import uz.yalla.maps.api.model.MapCapabilities
import uz.yalla.maps.api.model.MapStyle
import uz.yalla.maps.api.model.CenterPinState
import uz.yalla.maps.config.requireMaps

internal class SwitchingMapProvider(
    private val googleProvider: MapProvider,
    private val libreProvider: MapProvider
) : MapProvider {
    private val config = requireMaps()
    private var userSelectedProvider: MapProvider? = null
    private val currentProvider: MapProvider get() = userSelectedProvider ?: googleProvider

    init {
        config.scope.launch {
            config.mapKindPreference.collectLatest { type ->
                userSelectedProvider = when (type) {
                    MapKind.Google -> googleProvider
                    MapKind.Libre -> libreProvider
                }
            }
        }
    }

    override val type: MapKind
        get() = currentProvider.type
    override val capabilities: MapCapabilities
        get() = currentProvider.capabilities
    override val style: MapStyle
        get() = currentProvider.style

    override fun createLiteMap(): LiteMap = SwitchingLiteMap()

    override fun createExtendedMap(): ExtendedMap = SwitchingExtendedMap()

    override fun createStaticMap(): StaticMap = SwitchingStaticMap()

    override fun createController(): MapController = SwitchingMapController()
}

private class SwitchingLiteMap : LiteMap {
    private val config = requireMaps()
    private val mapKindPreference = config.mapKindPreference
    private val google by lazy { requireNotNull(config.factory).createGoogleProvider().createLiteMap() }
    private val libre by lazy { requireNotNull(config.factory).createLibreProvider().createLiteMap() }

    @Composable
    override fun Content(
        controller: MapController,
        modifier: Modifier,
        initialPoint: GeoPoint?,
        showLocationIndicator: Boolean,
        bindLocationTracker: Boolean,
        onCenterPinChanged: ((CenterPinState) -> Unit)?,
        onMapReady: (() -> Unit)?
    ) {
        val mapType by mapKindPreference.collectAsStateWithLifecycle(MapKind.Google)
        key(mapType) {
            when (mapType) {
                MapKind.Google ->
                    google.Content(
                        controller = controller,
                        modifier = modifier,
                        initialPoint = initialPoint,
                        showLocationIndicator = showLocationIndicator,
                        bindLocationTracker = bindLocationTracker,
                        onCenterPinChanged = onCenterPinChanged,
                        onMapReady = onMapReady
                    )
                MapKind.Libre ->
                    libre.Content(
                        controller = controller,
                        modifier = modifier,
                        initialPoint = initialPoint,
                        showLocationIndicator = showLocationIndicator,
                        bindLocationTracker = bindLocationTracker,
                        onCenterPinChanged = onCenterPinChanged,
                        onMapReady = onMapReady
                    )
            }
        }
    }
}

private class SwitchingExtendedMap : ExtendedMap {
    private val config = requireMaps()
    private val mapKindPreference = config.mapKindPreference
    private val google by lazy { requireNotNull(config.factory).createGoogleProvider().createExtendedMap() }
    private val libre by lazy { requireNotNull(config.factory).createLibreProvider().createExtendedMap() }

    @Composable
    override fun Content(
        controller: MapController,
        modifier: Modifier,
        route: List<GeoPoint>,
        locations: List<GeoPoint>,
        initialPoint: GeoPoint?,
        showLocationIndicator: Boolean,
        showMarkerLabels: Boolean,
        startMarkerLabel: String?,
        endMarkerLabel: String?,
        isInteractionEnabled: Boolean,
        useInternalCameraInitialization: Boolean,
        onCenterPinChanged: ((CenterPinState) -> Unit)?,
        onMapReady: (() -> Unit)?,
        content: @Composable MapScope.() -> Unit
    ) {
        val mapType by mapKindPreference.collectAsStateWithLifecycle(MapKind.Google)
        key(mapType) {
            when (mapType) {
                MapKind.Google ->
                    google.Content(
                        controller = controller,
                        modifier = modifier,
                        route = route,
                        locations = locations,
                        initialPoint = initialPoint,
                        showLocationIndicator = showLocationIndicator,
                        showMarkerLabels = showMarkerLabels,
                        startMarkerLabel = startMarkerLabel,
                        endMarkerLabel = endMarkerLabel,
                        isInteractionEnabled = isInteractionEnabled,
                        useInternalCameraInitialization = useInternalCameraInitialization,
                        onCenterPinChanged = onCenterPinChanged,
                        onMapReady = onMapReady,
                        content = content
                    )
                MapKind.Libre ->
                    libre.Content(
                        controller = controller,
                        modifier = modifier,
                        route = route,
                        locations = locations,
                        initialPoint = initialPoint,
                        showLocationIndicator = showLocationIndicator,
                        showMarkerLabels = showMarkerLabels,
                        startMarkerLabel = startMarkerLabel,
                        endMarkerLabel = endMarkerLabel,
                        isInteractionEnabled = isInteractionEnabled,
                        useInternalCameraInitialization = useInternalCameraInitialization,
                        onCenterPinChanged = onCenterPinChanged,
                        onMapReady = onMapReady,
                        content = content
                    )
            }
        }
    }
}

private class SwitchingStaticMap : StaticMap {
    private val config = requireMaps()
    private val mapKindPreference = config.mapKindPreference
    private val google by lazy { requireNotNull(config.factory).createGoogleProvider().createStaticMap() }
    private val libre by lazy { requireNotNull(config.factory).createLibreProvider().createStaticMap() }

    @Composable
    override fun Content(
        modifier: Modifier,
        route: List<GeoPoint>?,
        locations: List<GeoPoint>?,
        startLabel: String?,
        endLabel: String?,
        onMapReady: (() -> Unit)?
    ) {
        val mapType by mapKindPreference.collectAsStateWithLifecycle(MapKind.Google)
        key(mapType) {
            when (mapType) {
                MapKind.Google ->
                    google.Content(
                        modifier = modifier,
                        route = route,
                        locations = locations,
                        startLabel = startLabel,
                        endLabel = endLabel,
                        onMapReady = onMapReady
                    )
                MapKind.Libre ->
                    libre.Content(
                        modifier = modifier,
                        route = route,
                        locations = locations,
                        startLabel = startLabel,
                        endLabel = endLabel,
                        onMapReady = onMapReady
                    )
            }
        }
    }
}
