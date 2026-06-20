package uz.yalla.maps.util

/** Normalizes [headingDegrees] into the [0, 360) range. */
internal fun normalizeHeading(headingDegrees: Float): Float {
    var normalized = headingDegrees % 360f
    if (normalized < 0f) normalized += 360f
    return normalized
}

/** Returns [toDegrees] re-expressed relative to [fromDegrees] so the rotation takes the short way around. */
internal fun shortestHeadingPath(
    fromDegrees: Float,
    toDegrees: Float
): Float {
    var delta = normalizeHeading(toDegrees) - normalizeHeading(fromDegrees)
    if (delta > 180f) delta -= 360f
    if (delta < -180f) delta += 360f
    return fromDegrees + delta
}

/** Interpolates from [fromDegrees] toward [toDegrees] by [fraction] (clamped to [0,1]) along the short arc. */
internal fun interpolateHeading(
    fromDegrees: Float,
    toDegrees: Float,
    fraction: Float
): Float {
    val targetUnwrapped = shortestHeadingPath(fromDegrees, toDegrees)
    return fromDegrees + (targetUnwrapped - fromDegrees) * fraction.coerceIn(0f, 1f)
}
