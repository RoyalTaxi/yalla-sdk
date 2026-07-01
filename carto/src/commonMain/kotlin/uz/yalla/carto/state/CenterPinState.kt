package uz.yalla.carto.state

import uz.yalla.core.geo.GeoPoint

public data class CenterPinState(
    val point: GeoPoint = GeoPoint.Zero,
    val isMoving: Boolean = false,
    val isByUser: Boolean = false
) {
    public companion object {
        public val INITIAL: CenterPinState = CenterPinState()
    }
}
