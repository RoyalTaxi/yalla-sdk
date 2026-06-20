package uz.yalla.core.location

import uz.yalla.core.util.normalizedId

/** A waypoint's role within a route: the [START], an intermediate [POINT], or the [STOP]. */
// TODO(quality, needs-decision): M20 — START/POINT/STOP are the lone UPPER_SNAKE id-bearing enum
// among ~10 PascalCase siblings; they should be Start/Point/Stop. Blocked: the constants are
// committed enum entries in core.klib.api and are referenced by external consumers (YallaClient
// taxi/history feature modules), so renaming is a breaking .api change. Needs owner sign-off plus a
// coordinated rename of the call sites.
public enum class PointKind(
    public val id: String
) {
    START("start"),

    POINT("point"),

    STOP("stop");

    public companion object {
        /**
         * Decodes a wire id into a [PointKind], normalizing case/whitespace and defaulting to
         * [POINT] on unknown/null — matching every sibling `from` decoder in the SDK.
         */
        public fun from(id: String?): PointKind = entries.find { it.id == id.normalizedId() } ?: POINT
    }
}
