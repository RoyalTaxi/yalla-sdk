package uz.yalla.maps.api

import uz.yalla.maps.compose.CameraPositionState

interface MapScope {
    val cameraState: CameraPositionState
    val isGoogleMaps: Boolean
}

internal class MapScopeImpl(
    override val cameraState: CameraPositionState,
    override val isGoogleMaps: Boolean
) : MapScope
