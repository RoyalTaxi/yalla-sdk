package uz.yalla.maps.util

// TODO(quality, needs-decision): finding #18 — these heading helpers have no production consumer
// outside DriverMotionModel (tests only) and should be `internal`, but they are frozen in the
// committed `.api`/`.klib.api` dumps, so demoting them is a breaking dump change. Needs owner
// sign-off on a binary-API break.

/** Normalizes [headingDegrees] into the [0, 360) range. */
public fun normalizeHeading(headingDegrees: Float): Float {
    var normalized = headingDegrees % 360f
    if (normalized < 0f) normalized += 360f
    return normalized
}

/** Returns [toDegrees] re-expressed relative to [fromDegrees] so the rotation takes the short way around. */
public fun shortestHeadingPath(
    fromDegrees: Float,
    toDegrees: Float
): Float {
    var delta = normalizeHeading(toDegrees) - normalizeHeading(fromDegrees)
    if (delta > 180f) delta -= 360f
    if (delta < -180f) delta += 360f
    return fromDegrees + delta
}

/** Interpolates from [fromDegrees] toward [toDegrees] by [fraction] (clamped to [0,1]) along the short arc. */
public fun interpolateHeading(
    fromDegrees: Float,
    toDegrees: Float,
    fraction: Float
): Float {
    val targetUnwrapped = shortestHeadingPath(fromDegrees, toDegrees)
    return fromDegrees + (targetUnwrapped - fromDegrees) * fraction.coerceIn(0f, 1f)
}
