package uz.yalla.firebase.analytics

import uz.yalla.firebase.YallaFirebase

/**
 * Convenience top-level function for logging a named analytics event with optional parameters.
 *
 * Delegates to [YallaFirebase.analytics.logEvent][YallaAnalytics.logEvent]. An empty [params]
 * map is treated the same as no parameters — `null` is passed to the underlying SDK so that
 * Firebase does not record an empty parameter bundle.
 *
 * Prefer this function for one-off call-sites where constructing a full [AnalyticsEvent]
 * subclass would be verbose. For structured, reusable events use [AnalyticsEvent] and
 * [YallaAnalytics.log] instead.
 *
 * Usage example:
 * ```kotlin
 * trackEvent("app_open")
 * trackEvent("search", mapOf("query" to "taxi"))
 * ```
 *
 * @param name The Firebase event name.
 * @param params Optional map of event parameters. Defaults to an empty map (no params sent).
 * @see YallaAnalytics.logEvent
 * @see AnalyticsEvent.Custom
 * @since 0.0.1
 */
fun trackEvent(
    name: String,
    params: Map<String, Any> = emptyMap()
) {
    YallaFirebase.analytics.logEvent(name, params.ifEmpty { null })
}
