package uz.yalla.maps.provider.google

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.CoroutineScope
import org.koin.compose.koinInject
import uz.yalla.core.geo.GeoPoint
import uz.yalla.core.kind.ThemeKind
import uz.yalla.maps.api.ExtendedMap
import uz.yalla.maps.api.MapController
import uz.yalla.maps.api.MapScope
import uz.yalla.maps.api.MapScopeImpl
import uz.yalla.maps.api.model.MarkerState
import uz.yalla.maps.compose.CameraPositionState
import uz.yalla.maps.compose.rememberCameraPositionState
import uz.yalla.maps.config.MapConstants
import uz.yalla.maps.di.MapDependencies
import uz.yalla.maps.model.CameraPosition
import uz.yalla.maps.provider.common.CameraInitializationEffect
import uz.yalla.maps.provider.common.LocationTrackingEffect
import uz.yalla.maps.provider.common.rememberMapInitState
import uz.yalla.maps.provider.google.component.LocationIndicator
import uz.yalla.maps.provider.google.component.LocationsLayer
import uz.yalla.maps.provider.google.component.RouteLayer
import uz.yalla.maps.util.toGeoPoint

class GoogleExtendedMap : ExtendedMap {
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
        val googleController = controller.requireGoogleController()

        val dependencies: MapDependencies = koinInject()
        val scope = rememberCoroutineScope()
        val density = LocalDensity.current

        val themeType by dependencies.interfacePreferences.themeType.collectAsStateWithLifecycle(ThemeKind.System)
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

        ControllerBindingEffect(googleController, cameraState, scope, density)

        LocationTrackingEffect(dependencies.locationProvider, hasLocationPermission)

        if (useInternalCameraInitialization) {
            CameraInitializationEffect(
                pendingTarget = pendingTarget,
                userLocation = userLocation,
                hasCachedLocation = initialPoint != null,
                controller = googleController,
                onInitialized = { isUserLocation ->
                    initState.onMovedToLocation(isUserLocation)
                    initState.onInitialized()
                },
                onMarkerChanged = onMarkerChanged,
                onMapReady = onMapReady
            )
        }

        CameraTrackingEffect(cameraState, googleController, onMarkerChanged, initState.isInitialized)

        val contentPadding by googleController.contentPadding.collectAsStateWithLifecycle()

        MapContent(
            modifier = modifier,
            cameraState = cameraState,
            theme = themeType,
            isInteractionEnabled = isInteractionEnabled,
            route = route,
            locations = locations,
            showLocationIndicator = showLocationIndicator,
            showMarkerLabels = showMarkerLabels,
            startMarkerLabel = startMarkerLabel,
            endMarkerLabel = endMarkerLabel,
            userLocation = currentLocation,
            contentPadding = contentPadding,
            onMapSizeChanged = googleController::setMapSize,
            onMapReady = {
                googleController.onMapReady()
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
private fun rememberInitialCameraState(initialTarget: GeoPoint): CameraPositionState =
    rememberCameraPositionState {
        position =
            CameraPosition(
                target = initialTarget.toLatLng(),
                zoom = MapConstants.DEFAULT_ZOOM.toFloat()
            )
    }

@Composable
private fun ControllerBindingEffect(
    controller: GoogleMapController,
    cameraState: CameraPositionState,
    scope: CoroutineScope,
    density: Density
) {
    LaunchedEffect(cameraState) {
        controller.bind(cameraState, scope, density)
    }
}

@Composable
private fun CameraTrackingEffect(
    cameraState: CameraPositionState,
    controller: GoogleMapController,
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
    cameraState: CameraPositionState,
    theme: ThemeKind,
    isInteractionEnabled: Boolean,
    route: List<GeoPoint>,
    locations: List<GeoPoint>,
    showLocationIndicator: Boolean,
    showMarkerLabels: Boolean,
    startMarkerLabel: String?,
    endMarkerLabel: String?,
    userLocation: GeoPoint?,
    contentPadding: PaddingValues,
    onMapSizeChanged: ((IntSize) -> Unit)?,
    onMapReady: () -> Unit,
    content: @Composable MapScope.() -> Unit
) {
    val mapScope =
        remember(cameraState) {
            MapScopeImpl(cameraState, isGoogleMaps = true)
        }

    Box(modifier = modifier) {
        BaseMapContent(
            cameraState = cameraState,
            theme = theme,
            gesturesEnabled = isInteractionEnabled,
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPadding,
            onMapSizeChanged = onMapSizeChanged,
            onMapReady = onMapReady
        ) {
            RouteLayer(route)

            if (locations.size >= 2) {
                LocationsLayer(
                    arrival = null,
                    duration = null,
                    locations = locations,
                    startLabel = if (showMarkerLabels) startMarkerLabel else null,
                    endLabel = if (showMarkerLabels) endMarkerLabel else null
                )
            }

            if (showLocationIndicator) {
                LocationIndicator(userLocation)
            }

            mapScope.content()
        }
    }
}
