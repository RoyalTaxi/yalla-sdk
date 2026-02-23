package uz.yalla.maps.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.viewinterop.UIKitView
import cocoapods.GoogleMaps.GMSCameraPosition
import cocoapods.GoogleMaps.GMSMapView
import cocoapods.GoogleMaps.GMSMapViewDelegateProtocol
import cocoapods.GoogleMaps.GMSMapViewPaddingAdjustmentBehavior
import cocoapods.GoogleMaps.kGMSTypeHybrid
import cocoapods.GoogleMaps.kGMSTypeNone
import cocoapods.GoogleMaps.kGMSTypeNormal
import cocoapods.GoogleMaps.kGMSTypeSatellite
import cocoapods.GoogleMaps.kGMSTypeTerrain
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCSignatureOverride
import kotlinx.cinterop.useContents
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.UIKit.UIEdgeInsetsMake
import platform.UIKit.UIUserInterfaceStyle
import platform.darwin.NSObject
import uz.yalla.core.kind.ThemeKind
import uz.yalla.maps.model.MapProperties
import uz.yalla.maps.model.MapType
import uz.yalla.maps.model.MapUiSettings

@OptIn(ExperimentalForeignApi::class)
private class GMSMapViewDelegate(
    private val cameraPositionState: CameraPositionState,
    private val onMapLoaded: (() -> Unit)?,
) : NSObject(),
    GMSMapViewDelegateProtocol {
    private var hasReportedReady = false

    override fun mapView(
        mapView: GMSMapView,
        willMove: Boolean
    ) {
        cameraPositionState.isMoving = true
        cameraPositionState.cameraMoveStartedReason =
            if (willMove) {
                CameraMoveStartedReason.GESTURE
            } else {
                CameraMoveStartedReason.DEVELOPER_ANIMATION
            }
    }

    @ObjCSignatureOverride
    override fun mapView(
        mapView: GMSMapView,
        didChangeCameraPosition: GMSCameraPosition
    ) {
        updateCameraPositionStateOnMove(cameraPositionState, didChangeCameraPosition)
    }

    @ObjCSignatureOverride
    override fun mapView(
        mapView: GMSMapView,
        idleAtCameraPosition: GMSCameraPosition
    ) {
        updateCameraPositionStateOnIdle(cameraPositionState, idleAtCameraPosition)
    }

    override fun mapViewDidFinishTileRendering(mapView: GMSMapView) {
        if (!hasReportedReady) {
            hasReportedReady = true
            onMapLoaded?.invoke()
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun GoogleMap(
    modifier: Modifier,
    cameraPositionState: CameraPositionState,
    properties: MapProperties,
    uiSettings: MapUiSettings,
    theme: ThemeKind,
    contentPadding: PaddingValues,
    onMapLoaded: (() -> Unit)?,
    content: (
        @Composable @GoogleMapComposable
        () -> Unit
    )?,
) {
    val interfaceStyle =
        when (theme) {
            ThemeKind.Light -> UIUserInterfaceStyle.UIUserInterfaceStyleLight
            ThemeKind.Dark -> UIUserInterfaceStyle.UIUserInterfaceStyleDark
            ThemeKind.System -> UIUserInterfaceStyle.UIUserInterfaceStyleUnspecified
        }

    val mapView =
        remember {
            GMSMapView().apply {
                paddingAdjustmentBehavior = GMSMapViewPaddingAdjustmentBehavior.byValue(2u)
            }
        }

    val layoutDirection = LocalLayoutDirection.current
    val currentOnMapLoaded by rememberUpdatedState(onMapLoaded)

    val delegate =
        remember {
            GMSMapViewDelegate(
                cameraPositionState = cameraPositionState,
                onMapLoaded = { currentOnMapLoaded?.invoke() },
            )
        }

    val parentComposition = rememberCompositionContext()
    val currentContent by rememberUpdatedState(content)

    SetupCameraPositionStateSync(cameraPositionState, mapView)

    DisposableEffect(delegate) {
        mapView.delegate = delegate
        onDispose { mapView.delegate = null }
    }

    DisposableEffect(mapView, parentComposition) {
        var composition: Composition? = null
        if (currentContent != null) {
            composition = Composition(MapApplier(mapView), parentComposition)
            composition.setContent { currentContent?.invoke() }
        }
        onDispose { composition?.dispose() }
    }

    UIKitView(
        factory = {
            mapView.apply {
                camera =
                    GMSCameraPosition.cameraWithTarget(
                        target =
                            CLLocationCoordinate2DMake(
                                cameraPositionState.position.target.latitude,
                                cameraPositionState.position.target.longitude
                            ),
                        zoom = cameraPositionState.position.zoom,
                        bearing = cameraPositionState.position.bearing.toDouble(),
                        viewingAngle = cameraPositionState.position.tilt.toDouble()
                    )
                mapType = properties.mapType.toGMSMapType()
                overrideUserInterfaceStyle = interfaceStyle
                buildingsEnabled = properties.isBuildingEnabled
                indoorEnabled = properties.isIndoorEnabled
                trafficEnabled = properties.isTrafficEnabled
                myLocationEnabled = properties.isMyLocationEnabled
                settings.compassButton = uiSettings.compassEnabled
                settings.indoorPicker = uiSettings.indoorLevelPickerEnabled
                settings.myLocationButton = uiSettings.myLocationButtonEnabled
                settings.rotateGestures = uiSettings.rotationGesturesEnabled
                settings.scrollGestures = uiSettings.scrollGesturesEnabled
                settings.tiltGestures = uiSettings.tiltGesturesEnabled
                settings.zoomGestures = uiSettings.zoomGesturesEnabled
                padding =
                    UIEdgeInsetsMake(
                        contentPadding.calculateTopPadding().value.toDouble(),
                        contentPadding.calculateLeftPadding(layoutDirection).value.toDouble(),
                        contentPadding.calculateBottomPadding().value.toDouble(),
                        contentPadding.calculateRightPadding(layoutDirection).value.toDouble()
                    )
                this.delegate = delegate
            }
        },
        modifier = modifier,
        update = { view ->
            view.mapType = properties.mapType.toGMSMapType()
            view.overrideUserInterfaceStyle = interfaceStyle
            view.buildingsEnabled = properties.isBuildingEnabled
            view.indoorEnabled = properties.isIndoorEnabled
            view.trafficEnabled = properties.isTrafficEnabled
            view.myLocationEnabled = properties.isMyLocationEnabled
            view.settings.compassButton = uiSettings.compassEnabled
            view.settings.indoorPicker = uiSettings.indoorLevelPickerEnabled
            view.settings.myLocationButton = uiSettings.myLocationButtonEnabled
            view.settings.rotateGestures = uiSettings.rotationGesturesEnabled
            view.settings.scrollGestures = uiSettings.scrollGesturesEnabled
            view.settings.tiltGestures = uiSettings.tiltGesturesEnabled
            view.settings.zoomGestures = uiSettings.zoomGesturesEnabled

            val newTop = contentPadding.calculateTopPadding().value.toDouble()
            val newLeft = contentPadding.calculateLeftPadding(layoutDirection).value.toDouble()
            val newBottom = contentPadding.calculateBottomPadding().value.toDouble()
            val newRight = contentPadding.calculateRightPadding(layoutDirection).value.toDouble()

            val (currentTop, currentLeft, currentBottom, currentRight) =
                view.padding.useContents {
                    arrayOf(top, left, bottom, right)
                }
            val isPaddingChanged =
                currentTop != newTop ||
                    currentLeft != newLeft ||
                    currentBottom != newBottom ||
                    currentRight != newRight

            if (isPaddingChanged) {
                val isPaddingDecreasing =
                    newTop < currentTop || newLeft < currentLeft || newBottom < currentBottom || newRight < currentRight
                if (isPaddingDecreasing) {
                    view.padding = UIEdgeInsetsMake(0.0, 0.0, 0.0, 0.0)
                }
                view.padding = UIEdgeInsetsMake(newTop, newLeft, newBottom, newRight)
            }
        }
    )
}

@OptIn(ExperimentalForeignApi::class)
private fun MapType.toGMSMapType() =
    when (this) {
        MapType.NONE -> kGMSTypeNone
        MapType.NORMAL -> kGMSTypeNormal
        MapType.SATELLITE -> kGMSTypeSatellite
        MapType.HYBRID -> kGMSTypeHybrid
        MapType.TERRAIN -> kGMSTypeTerrain
    }
