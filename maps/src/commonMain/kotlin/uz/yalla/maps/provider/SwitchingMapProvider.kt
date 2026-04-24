package uz.yalla.maps.provider

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.yalla.core.preferences.InterfacePreferences
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
import uz.yalla.maps.api.model.MarkerState
import uz.yalla.maps.provider.google.GoogleExtendedMap
import uz.yalla.maps.provider.google.GoogleLiteMap
import uz.yalla.maps.provider.google.GoogleStaticMap
import uz.yalla.maps.provider.libre.LibreExtendedMap
import uz.yalla.maps.provider.libre.LibreLiteMap
import uz.yalla.maps.provider.libre.LibreStaticMap

/**
 * [MapProvider] that delegates to Google or Libre at runtime based on user preference.
 *
 * Observes [InterfacePreferences.mapKind] and switches the active backend accordingly.
 * Map composables it creates also observe the preference and swap implementations in place.
 *
 * ## Lifecycle
 *
 * The provider does not own its coroutine scope. Pass a [CoroutineScope] whose lifetime
 * matches the desired observation window (typically a process-lifetime scope in the Koin
 * graph). When that scope is cancelled the preference observation stops automatically.
 * No explicit cleanup call is required on the provider itself.
 *
 * Controllers created via [createController] inherit the same scope so their
 * internal preference observation is also tied to the caller-managed lifetime.
 *
 * @param googleProvider Concrete Google Maps backend.
 * @param libreProvider Concrete MapLibre backend.
 * @param interfacePreferences Source of the user's map provider preference.
 * @param scope Caller-owned scope that governs the lifetime of preference observation
 *   inside this provider and any controllers it creates. Cancelling this scope stops
 *   all coroutines launched by this provider.
 * @since 0.0.1
 * @see SwitchingMapController
 */
class SwitchingMapProvider(
    private val googleProvider: MapProvider,
    private val libreProvider: MapProvider,
    private val interfacePreferences: InterfacePreferences,
    private val scope: CoroutineScope,
) : MapProvider {
    private var userSelectedProvider: MapProvider? = null
    private val currentProvider: MapProvider get() = userSelectedProvider ?: googleProvider

    init {
        scope.launch {
            interfacePreferences.mapKind.collectLatest { type ->
                userSelectedProvider =
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

    override fun createController(): MapController = SwitchingMapController(interfacePreferences, scope)
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
