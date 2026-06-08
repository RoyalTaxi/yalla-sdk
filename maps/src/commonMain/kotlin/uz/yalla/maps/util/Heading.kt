package uz.yalla.maps.util

fun normalizeHeading(headingDegrees: Float): Float {
    var normalized = headingDegrees % 360f
    if (normalized < 0f) normalized += 360f
    return normalized
}

fun shortestHeadingPath(fromDegrees: Float, toDegrees: Float): Float {
    val from = normalizeHeading(fromDegrees)
    val to = normalizeHeading(toDegrees)
    var delta = to - from
    if (delta > 180f) delta -= 360f
    if (delta < -180f) delta += 360f
    return from + delta
}

fun interpolateHeading(fromDegrees: Float, toDegrees: Float, fraction: Float): Float {
    val targetUnwrapped = shortestHeadingPath(fromDegrees, toDegrees)
    return fromDegrees + (targetUnwrapped - fromDegrees) * fraction.coerceIn(0f, 1f)
}
