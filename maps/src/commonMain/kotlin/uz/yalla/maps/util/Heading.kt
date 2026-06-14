package uz.yalla.maps.util

public fun normalizeHeading(headingDegrees: Float): Float {
    var normalized = headingDegrees % 360f
    if (normalized < 0f) normalized += 360f
    return normalized
}

public fun shortestHeadingPath(fromDegrees: Float, toDegrees: Float): Float {
    var delta = normalizeHeading(toDegrees) - normalizeHeading(fromDegrees)
    if (delta > 180f) delta -= 360f
    if (delta < -180f) delta += 360f
    return fromDegrees + delta
}

public fun interpolateHeading(fromDegrees: Float, toDegrees: Float, fraction: Float): Float {
    val targetUnwrapped = shortestHeadingPath(fromDegrees, toDegrees)
    return fromDegrees + (targetUnwrapped - fromDegrees) * fraction.coerceIn(0f, 1f)
}
