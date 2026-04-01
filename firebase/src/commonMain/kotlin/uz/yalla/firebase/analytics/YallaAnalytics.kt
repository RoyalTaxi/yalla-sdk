package uz.yalla.firebase.analytics

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.analytics.FirebaseAnalytics
import dev.gitlive.firebase.analytics.analytics
import uz.yalla.firebase.YallaFirebase
import uz.yalla.firebase.crashlytics.YallaCrashlytics

/**
 * Wrapper for Firebase Analytics that provides safe event logging with built-in error handling.
 *
 * All operations are wrapped in `try-catch` blocks. Exceptions thrown by the underlying
 * Firebase SDK are swallowed and forwarded to [YallaFirebase.logger] — they do not propagate
 * to the caller. This ensures that analytics failures never crash the app.
 *
 * Access this class via [YallaFirebase.analytics]; do not instantiate it directly.
 *
 * @see YallaFirebase.analytics
 * @see AnalyticsEvent
 * @see trackEvent
 * @since 0.0.1
 */
class YallaAnalytics {
    private val analytics: FirebaseAnalytics by lazy { Firebase.analytics }

    /**
     * Logs a custom event with an optional parameter map to Firebase Analytics.
     *
     * If the underlying Firebase SDK throws (e.g. due to initialization issues or invalid
     * event names), the exception is caught and forwarded to [YallaFirebase.logger] under
     * the `"Analytics"` tag. The caller is not notified of the failure.
     *
     * @param name The event name. Must follow Firebase naming rules (max 40 chars,
     *   alphanumeric + underscores, must start with a letter).
     * @param params Optional map of event parameters (max 25 per event, values must be
     *   `String`, `Long`, or `Double`). Pass `null` to send no parameters.
     * @see log
     * @see trackEvent
     * @since 0.0.1
     */
    fun logEvent(
        name: String,
        params: Map<String, Any>? = null
    ) {
        try {
            analytics.logEvent(name, params)
        } catch (e: Exception) {
            YallaFirebase.logger.log("Analytics", "Failed to log event '$name': ${e.message}")
        }
    }

    /**
     * Logs a predefined [AnalyticsEvent] to Firebase Analytics.
     *
     * Convenience wrapper around [logEvent] that extracts [AnalyticsEvent.name] and
     * [AnalyticsEvent.params] from the sealed class instance.
     *
     * @param event The analytics event to log. Use [AnalyticsEvent] subclasses for
     *   structured events or [AnalyticsEvent.Custom] for arbitrary events.
     * @see logEvent
     * @see AnalyticsEvent
     * @since 0.0.1
     */
    fun log(event: AnalyticsEvent) {
        logEvent(event.name, event.params)
    }

    /**
     * Sets the user ID for analytics tracking.
     *
     * Associates subsequent events with this user identifier in the Analytics dashboard.
     * Pass `null` to clear the user ID (e.g. on logout).
     *
     * This is a `suspend` function because the underlying GitLive Firebase SDK exposes
     * this operation as a coroutine.
     *
     * @param userId The user identifier to associate with events, or `null` to clear it.
     * @see setUserProperty
     * @since 0.0.1
     */
    suspend fun setUserId(userId: String?) {
        try {
            analytics.setUserId(userId)
        } catch (e: Exception) {
            YallaFirebase.logger.log("Analytics", "Failed to set user ID: ${e.message}")
        }
    }

    /**
     * Sets a user property for analytics segmentation.
     *
     * User properties are attributes that describe the user (e.g. `"subscription_tier"`
     * to `"premium"`). Firebase Analytics supports up to 25 custom user properties.
     *
     * This is a `suspend` function because the underlying GitLive Firebase SDK exposes
     * this operation as a coroutine.
     *
     * @param name The property name (max 24 chars, alphanumeric + underscores).
     * @param value The property value (max 36 chars).
     * @see setUserId
     * @since 0.0.1
     */
    suspend fun setUserProperty(
        name: String,
        value: String
    ) {
        try {
            analytics.setUserProperty(name, value)
        } catch (e: Exception) {
            YallaFirebase.logger.log("Analytics", "Failed to set user property '$name': ${e.message}")
        }
    }

    /**
     * Enables or disables Firebase Analytics data collection.
     *
     * Use this to honour user consent preferences (e.g. GDPR opt-out). When disabled,
     * no data is sent to Firebase. The setting persists across app restarts.
     *
     * @param enabled `true` to enable data collection, `false` to disable it.
     * @see YallaCrashlytics.setCrashlyticsCollectionEnabled
     * @since 0.0.1
     */
    fun setAnalyticsCollectionEnabled(enabled: Boolean) {
        try {
            analytics.setAnalyticsCollectionEnabled(enabled)
        } catch (e: Exception) {
            YallaFirebase.logger.log("Analytics", "Failed to set collection enabled: ${e.message}")
        }
    }
}
