package uz.yalla.firebase.analytics

/**
 * Predefined analytics events for consistent tracking across platforms.
 *
 * Extend this sealed class to define structured event types with validated parameter sets.
 * Use [Custom] for one-off events that don't fit predefined shapes. Pass any subclass
 * directly to [YallaAnalytics.log].
 *
 * Usage example:
 * ```kotlin
 * // Predefined events
 * analytics.log(AnalyticsEvent.ScreenView("Home"))
 * analytics.log(AnalyticsEvent.ScreenView("Profile", screenClass = "ProfileActivity"))
 * analytics.log(AnalyticsEvent.ButtonClick("login"))
 * analytics.log(AnalyticsEvent.ButtonClick("share", source = "feed"))
 *
 * // Custom event
 * analytics.log(AnalyticsEvent.Custom("purchase", mapOf("amount" to 4990, "currency" to "UZS")))
 * ```
 *
 * @property name The Firebase event name as it will appear in the Analytics dashboard.
 * @property params Optional map of event parameters. `null` means no parameters are sent.
 * @see YallaAnalytics.log
 * @see YallaAnalytics.logEvent
 * @since 0.0.1
 */
sealed class AnalyticsEvent(
    open val name: String,
    open val params: Map<String, Any>? = null
) {
    /**
     * Screen view event, equivalent to Firebase's `screen_view` event.
     *
     * Tracks navigation between screens. The [screenName] is required; [screenClass]
     * (usually the class/component name) is optional and excluded from params when `null`.
     *
     * @property screenName Human-readable name of the screen (e.g. `"Home"`, `"Checkout"`).
     * @property screenClass Optional class or component name (e.g. `"HomeActivity"`).
     * @see YallaAnalytics.log
     * @since 0.0.1
     */
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

    /**
     * Button click event, equivalent to a custom `button_click` event.
     *
     * Tracks user interaction with UI controls. [buttonName] is required; [source]
     * provides context about where the button lives (e.g. `"header"`, `"bottom_sheet"`)
     * and is excluded from params when `null`.
     *
     * @property buttonName Identifier for the button (e.g. `"login"`, `"confirm_payment"`).
     * @property source Optional context about the button's location in the UI.
     * @see YallaAnalytics.log
     * @since 0.0.1
     */
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

    /**
     * Fully custom event with an arbitrary name and parameter map.
     *
     * Use this for domain-specific events that don't match any predefined type.
     * [name] and [params] are both user-supplied. [params] defaults to `null`
     * (no parameters) when omitted.
     *
     * @property name The event name as it will appear in the Analytics dashboard.
     * @property params Optional map of arbitrary event parameters.
     * @see YallaAnalytics.log
     * @see trackEvent
     * @since 0.0.1
     */
    data class Custom(
        override val name: String,
        override val params: Map<String, Any>? = null
    ) : AnalyticsEvent(name, params) {
        /**
         * Creates a custom event with a name only and no parameters.
         *
         * Shorthand for `Custom(eventName, null)`.
         *
         * @param eventName The event name as it will appear in the Analytics dashboard.
         * @since 0.0.1
         */
        constructor(eventName: String) : this(eventName, null)
    }
}
