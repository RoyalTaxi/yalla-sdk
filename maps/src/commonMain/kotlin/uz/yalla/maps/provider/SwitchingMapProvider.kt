package uz.yalla.maps.provider

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.yalla.core.contract.preferences.InterfacePreferences
import uz.yalla.core.geo.GeoPoint
import uz.yalla.core.kind.MapKind
import uz.yalla.maps.api.ExtendedMap
import uz.yalla.maps.api.LiteMap
import uz.yalla.maps.api.MapController
import uz.yalla.maps.api.MapProvider
import uz.yalla.maps.api.MapScope
import uz.yalla.maps.api.StaticMap
import uz.yalla.maps.api.model.MapCapabilities
import uz.yalla.maps.api.model.MapStyle
import uz.yalla.maps.api.model.MarkerState
import uz.yalla.maps.provider.google.GoogleExtendedMap
import uz.yalla.maps.provider.google.GoogleLiteMap
import uz.yalla.maps.provider.google.GoogleMapProvider
import uz.yalla.maps.provider.google.GoogleStaticMap
import uz.yalla.maps.provider.libre.LibreExtendedMap
import uz.yalla.maps.provider.libre.LibreLiteMap
import uz.yalla.maps.provider.libre.LibreMapProvider
import uz.yalla.maps.provider.libre.LibreStaticMap

class SwitchingMapProvider(
    private val interfacePreferences: InterfacePreferences
) : MapProvider {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val googleProvider by lazy { GoogleMapProvider() }
    private val libreProvider by lazy { LibreMapProvider() }
    private var _currentProvider: MapProvider? = null
    private val currentProvider: MapProvider get() = _currentProvider ?: googleProvider

    init {
        scope.launch {
            interfacePreferences.mapKind.collectLatest { type ->
                _currentProvider =
                    when (type) {
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

    override fun createLiteMap(): LiteMap = SwitchingLiteMap(interfacePreferences)

    override fun createExtendedMap(): ExtendedMap = SwitchingExtendedMap(interfacePreferences)

    override fun createStaticMap(): StaticMap = SwitchingStaticMap(interfacePreferences)

    override fun createController(): MapController = SwitchingMapController(interfacePreferences)
}

private class SwitchingLiteMap(
    private val interfacePreferences: InterfacePreferences
) : LiteMap {
    private val google by lazy { GoogleLiteMap() }
    private val libre by lazy { LibreLiteMap() }

    @Composable
    override fun Content(
        controller: MapController,
        modifier: Modifier,
        initialPoint: GeoPoint?,
        showLocationIndicator: Boolean,
        bindLocationTracker: Boolean,
        onMarkerChanged: ((MarkerState) -> Unit)?,
        onMapReady: (() -> Unit)?
    ) {
        val mapType by interfacePreferences.mapKind.collectAsStateWithLifecycle(MapKind.Google)
        key(mapType) {
            when (mapType) {
                MapKind.Google ->
                    google.Content(
                        controller = controller,
                        modifier = modifier,
                        initialPoint = initialPoint,
                        showLocationIndicator = showLocationIndicator,
                        bindLocationTracker = bindLocationTracker,
                        onMarkerChanged = onMarkerChanged,
                        onMapReady = onMapReady
                    )
                MapKind.Libre ->
                    libre.Content(
                        controller = controller,
                        modifier = modifier,
                        initialPoint = initialPoint,
                        showLocationIndicator = showLocationIndicator,
                        bindLocationTracker = bindLocationTracker,
                        onMarkerChanged = onMarkerChanged,
                        onMapReady = onMapReady
                    )
            }
        }
    }
}

private class SwitchingExtendedMap(
    private val interfacePreferences: InterfacePreferences
) : ExtendedMap {
    private val google by lazy { GoogleExtendedMap() }
    private val libre by lazy { LibreExtendedMap() }

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
        onMarkerChanged: ((MarkerState) -> Unit)?,
        onMapReady: (() -> Unit)?,
        content: @Composable MapScope.() -> Unit
    ) {
        val mapType by interfacePreferences.mapKind.collectAsStateWithLifecycle(MapKind.Google)
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
                        onMarkerChanged = onMarkerChanged,
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
                        onMarkerChanged = onMarkerChanged,
                        onMapReady = onMapReady,
                        content = content
                    )
            }
        }
    }
}

private class SwitchingStaticMap(
    private val interfacePreferences: InterfacePreferences
) : StaticMap {
    private val google by lazy { GoogleStaticMap() }
    private val libre by lazy { LibreStaticMap() }

    @Composable
    override fun Content(
        modifier: Modifier,
        route: List<GeoPoint>?,
        locations: List<GeoPoint>?,
        startLabel: String?,
        endLabel: String?,
        onMapReady: (() -> Unit)?
    ) {
        val mapType by interfacePreferences.mapKind.collectAsStateWithLifecycle(MapKind.Google)
        key(mapType) {
            when (mapType) {
                MapKind.Google -> google.Content(
                    modifier = modifier,
                    route = route,
                    locations = locations,
                    startLabel = startLabel,
                    endLabel = endLabel,
                    onMapReady = onMapReady
                )
                MapKind.Libre -> libre.Content(
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
