package uz.yalla.maps.provider.libre

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.dellisd.spatialk.geojson.Position
import kotlinx.coroutines.CoroutineScope
import org.koin.compose.koinInject
import org.maplibre.compose.camera.CameraState
import org.maplibre.compose.camera.rememberCameraState
import uz.yalla.core.geo.GeoPoint
import uz.yalla.core.kind.ThemeKind
import uz.yalla.maps.api.ExtendedMap
import uz.yalla.maps.api.MapController
import uz.yalla.maps.api.MapScope
import uz.yalla.maps.api.MapScopeImpl
import uz.yalla.maps.api.model.MarkerState
import uz.yalla.maps.compose.CameraPositionState
import uz.yalla.maps.config.MapConstants
import uz.yalla.maps.di.MapDependencies
import uz.yalla.maps.provider.common.CameraInitializationEffect
import uz.yalla.maps.provider.common.LocationTrackingEffect
import uz.yalla.maps.provider.common.rememberMapInitState
import uz.yalla.maps.provider.libre.component.LocationIndicator
import uz.yalla.maps.provider.libre.component.LocationsLayer
import uz.yalla.maps.provider.libre.component.RouteLayer
import uz.yalla.maps.util.toGeoPoint
import org.maplibre.compose.camera.CameraPosition as LibreCameraPosition

class LibreExtendedMap : ExtendedMap {
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
        val libreController = controller.requireLibreController()

        val dependencies: MapDependencies = koinInject()
        val scope = rememberCoroutineScope()

        val themeType by dependencies.interfacePreferences.themeType.collectAsStateWithLifecycle(ThemeKind.System)
        val theme = rememberMapTheme(themeType)

        val currentLocation by dependencies.locationProvider.currentLocation.collectAsStateWithLifecycle(null)

        val userLocation =
            remember(currentLocation) {
                currentLocation?.takeIf { it != GeoPoint.Zero }
            }
        val hasLocationPermission = userLocation != null
        val fallback = initialPoint ?: MapConstants.BOBUR_SQUARE.toGeoPoint()

        val initState = rememberMapInitState()
        val initialTarget =
            remember(initialPoint, userLocation, fallback, useInternalCameraInitialization) {
                when {
                    initialPoint != null -> initialPoint
                    !useInternalCameraInitialization -> MapConstants.BOBUR_SQUARE.toGeoPoint()
                    userLocation != null -> userLocation
                    else -> fallback
                }
            }
        val cameraState = rememberInitialCameraState(initialTarget)

        val pendingTarget = remember(
            initState.isMapReady,
            initState.hasMovedToLocation,
            initState.hasMovedToUserLocation,
            initialPoint,
            userLocation,
            fallback,
            useInternalCameraInitialization
        ) {
            if (!useInternalCameraInitialization) return@remember null

            when {
                !initState.isMapReady -> null
                initialPoint != null && !initState.hasMovedToLocation -> initialPoint
                initialPoint != null -> null
                initState.hasMovedToUserLocation -> null
                userLocation != null -> userLocation
                !initState.hasMovedToLocation -> fallback
                else -> null
            }
        }

        ControllerBindingEffect(libreController, cameraState, scope)

        LocationTrackingEffect(dependencies.locationProvider, hasLocationPermission)

        if (useInternalCameraInitialization) {
            CameraInitializationEffect(
                pendingTarget = pendingTarget,
                userLocation = userLocation,
                hasCachedLocation = initialPoint != null,
                controller = libreController,
                onInitialized = { isUserLocation ->
                    initState.onMovedToLocation(isUserLocation)
                    initState.onInitialized()
                },
                onMarkerChanged = onMarkerChanged,
                onMapReady = onMapReady
            )
        }

        CameraTrackingEffect(cameraState, libreController, onMarkerChanged, initState.isInitialized)

        MapContent(
            modifier = modifier,
            cameraState = cameraState,
            theme = theme,
            isInteractionEnabled = isInteractionEnabled,
            route = route,
            locations = locations,
            showLocationIndicator = showLocationIndicator,
            showMarkerLabels = showMarkerLabels,
            startMarkerLabel = startMarkerLabel,
            endMarkerLabel = endMarkerLabel,
            userLocation = currentLocation,
            onMapReady = {
                libreController.onMapReady()
                initState.onMapReady()
                if (!useInternalCameraInitialization && !initState.isInitialized) {
                    initState.onInitialized()
                    onMapReady?.invoke()
                }
            },
            content = content
        )
    }
}

@Composable
private fun rememberInitialCameraState(initialTarget: GeoPoint): CameraState =
    rememberCameraState(
        firstPosition =
            LibreCameraPosition(
                target = Position(latitude = initialTarget.lat, longitude = initialTarget.lng),
                zoom = MapConstants.DEFAULT_ZOOM
            )
    )

@Composable
private fun ControllerBindingEffect(
    controller: LibreMapController,
    cameraState: CameraState,
    scope: CoroutineScope
) {
    LaunchedEffect(cameraState) {
        controller.bind(cameraState, scope)
    }
}

@Composable
private fun CameraTrackingEffect(
    cameraState: CameraState,
    controller: LibreMapController,
    onMarkerChanged: ((MarkerState) -> Unit)?,
    isEnabled: Boolean
) {
    if (isEnabled) {
        CameraTrackingEffect(cameraState, controller, onMarkerChanged)
    }
}

@Composable
private fun MapContent(
    modifier: Modifier,
    cameraState: CameraState,
    theme: ThemeKind,
    isInteractionEnabled: Boolean,
    route: List<GeoPoint>,
    locations: List<GeoPoint>,
    showLocationIndicator: Boolean,
    showMarkerLabels: Boolean,
    startMarkerLabel: String?,
    endMarkerLabel: String?,
    userLocation: GeoPoint?,
    onMapReady: () -> Unit,
    content: @Composable MapScope.() -> Unit
) {
    val adaptedCameraState = remember { CameraPositionState() }
    LaunchedEffect(cameraState) {
        snapshotFlow { cameraState.position }
            .collect { pos ->
                adaptedCameraState.rawPosition = uz.yalla.maps.model.CameraPosition(
                    target = uz.yalla.maps.model.LatLng(pos.target.latitude, pos.target.longitude),
                    zoom = pos.zoom.toFloat()
                )
            }
    }

    val mapScope = remember(adaptedCameraState) {
        MapScopeImpl(
            cameraState = adaptedCameraState,
            isGoogleMaps = false
        )
    }

    Box(modifier = modifier) {
        BaseMapContent(
            cameraState = cameraState,
            theme = theme,
            gesturesEnabled = isInteractionEnabled,
            modifier = Modifier.fillMaxSize(),
            onMapReady = onMapReady
        ) {
            RouteLayer(route)

            LocationsLayer(
                arrival = null,
                duration = null,
                locations = locations,
                startLabel = if (showMarkerLabels) startMarkerLabel else null,
                endLabel = if (showMarkerLabels) endMarkerLabel else null
            )

            if (showLocationIndicator) {
                LocationIndicator(userLocation, cameraState)
            }

            mapScope.content()
        }
    }
}
