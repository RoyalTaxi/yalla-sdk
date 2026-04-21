package uz.yalla.platform.model

/**
 * Abstract icon identifiers shared between common and platform code.
 *
 * Platform button composables accept [IconType] instead of raw image resources,
 * allowing common code to specify icons without depending on platform-specific
 * drawable systems. The mapping to actual vectors happens in
 * [toImageVector][uz.yalla.platform.toImageVector].
 *
 * @since 0.0.1
 */
enum class IconType {
    /** Hamburger menu icon. */
    MENU,

    /** Close / dismiss (X) icon. */
    CLOSE,

    /** Checkmark / confirm icon. */
    DONE,

    /** Left arrow / back navigation icon. */
    BACK,

    /** Focus the map on the user's current location. */
    FOCUS_LOCATION,

    /** Focus the map to show the full route. */
    FOCUS_ROUTE,

    /** Focus the map on the ride origin point. */
    FOCUS_ORIGIN,

    /** Focus the map on the ride destination point. */
    FOCUS_DESTINATION,
}
