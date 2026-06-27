package uz.yalla.maps.motion

/**
 * How a platform marker-motion driver animates driver markers between sparse GPS fixes.
 *
 * This is the type-safe replacement for the `routeFollowingEnabled: Boolean` flag that every
 * platform driver used to default to `false`. That default was load-bearing and regressed to OFF
 * twice in production (Android never set it; iOS lost it to Swift lazy-capture). Making the mode a
 * required constructor parameter with no default means a controller cannot silently fall back to the
 * wrong behaviour — the wiring is pinned by the type system instead of a text-scanning fitness test.
 */
public sealed interface MotionMode {
    /**
     * The car is glued to the drawn route: position is sampled along the polyline's arc-length,
     * heading is the route tangent, and the remaining route is trimmed behind the car. Off-route the
     * model falls back to chord interpolation so the car never freezes. This is the production mode.
     */
    public data object RouteFollowing : MotionMode

    /**
     * Pure straight-line (chord) interpolation between successive GPS fixes, with no route awareness.
     * The original behaviour; the safe fallback when route-following is intentionally not wanted.
     */
    public data object ChordOnly : MotionMode
}
