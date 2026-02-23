package uz.yalla.firebase.analytics

/**
 * Predefined analytics events for consistent tracking across platforms.
 */
sealed class AnalyticsEvent(
    open val name: String,
    open val params: Map<String, Any>? = null
) {
    // Screen events
    data class ScreenView(
        val screenName: String,
        val screenClass: String? = null
    ) : AnalyticsEvent(
            name = "screen_view",
            params =
                buildMap {
                    put("screen_name", screenName)
                    screenClass?.let { put("screen_class", it) }
                }
        )

    // User action events
    data class ButtonClick(
        val buttonName: String,
        val source: String? = null
    ) : AnalyticsEvent(
            name = "button_click",
            params =
                buildMap {
                    put("button_name", buttonName)
                    source?.let { put("source", it) }
                }
        )

    // Custom event
    data class Custom(
        override val name: String,
        override val params: Map<String, Any>? = null
    ) : AnalyticsEvent(name, params) {
        constructor(eventName: String) : this(eventName, null)
    }

    companion object {
        const val PREFIX_CUSTOM = "custom_"
    }
}
