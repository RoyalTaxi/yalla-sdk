package uz.yalla.maps.motion

import uz.yalla.core.geo.GeoPoint

/** An interpolated marker pose sampled from [DriverMotionModel]: a position and a heading in degrees. */
public data class Pose(
    val point: GeoPoint,
    val bearing: Float
)
