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
import uz.yalla.core.contract.MapPreferences
import uz.yalla.core.geo.GeoPoint
import uz.yalla.core.kind.MapKind
import uz.yalla.maps.api.ExtendedMap
import uz.yalla.maps.api.LiteMap
import uz.yalla.maps.api.MapController
import uz.yalla.maps.api.MapProvider
import uz.yalla.maps.api.MapScope
import uz.yalla.maps.api.model.MapCapabilities
import uz.yalla.maps.api.model.MapStyle
import uz.yalla.maps.api.model.MarkerState
import uz.yalla.maps.provider.google.GoogleExtendedMap
import uz.yalla.maps.provider.google.GoogleLiteMap
import uz.yalla.maps.provider.google.GoogleMapProvider
import uz.yalla.maps.provider.libre.LibreExtendedMap
import uz.yalla.maps.provider.libre.LibreLiteMap
import uz.yalla.maps.provider.libre.LibreMapProvider

class SwitchingMapProvider(
    private val mapPreferences: MapPreferences
) : MapProvider {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val googleProvider = GoogleMapProvider()
    private val libreProvider = LibreMapProvider()
    private var currentProvider: MapProvider = googleProvider

    init {
        scope.launch {
            mapPreferences.mapKind.collectLatest { type ->
                currentProvider =
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

    override fun createLiteMap(): LiteMap = SwitchingLiteMap(mapPreferences)

    override fun createExtendedMap(): ExtendedMap = SwitchingExtendedMap(mapPreferences)

    override fun createController(): MapController = SwitchingMapController(mapPreferences)
}

private class SwitchingLiteMap(
    private val mapPreferences: MapPreferences
) : LiteMap {
    private val google = GoogleLiteMap()
    private val libre = LibreLiteMap()

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
        val mapType by mapPreferences.mapKind.collectAsStateWithLifecycle(MapKind.Google)
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
    private val mapPreferences: MapPreferences
) : ExtendedMap {
    private val google = GoogleExtendedMap()
    private val libre = LibreExtendedMap()

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
        val mapType by mapPreferences.mapKind.collectAsStateWithLifecycle(MapKind.Google)
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
