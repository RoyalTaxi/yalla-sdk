package uz.yalla.firebase.analytics

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.analytics.FirebaseAnalytics
import dev.gitlive.firebase.analytics.analytics
import uz.yalla.firebase.YallaFirebase

/**
 * Wrapper for Firebase Analytics with common logging operations.
 */
class YallaAnalytics {
    private val analytics: FirebaseAnalytics by lazy { Firebase.analytics }

    /**
     * Log a custom event with optional parameters.
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
     * Log a predefined AnalyticsEvent.
     */
    fun log(event: AnalyticsEvent) {
        logEvent(event.name, event.params)
    }

    /**
     * Set user ID for analytics tracking.
     */
    suspend fun setUserId(userId: String?) {
        try {
            analytics.setUserId(userId)
        } catch (e: Exception) {
            YallaFirebase.logger.log("Analytics", "Failed to set user ID: ${e.message}")
        }
    }

    /**
     * Set a user property.
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
     * Enable or disable analytics collection.
     */
    fun setAnalyticsCollectionEnabled(enabled: Boolean) {
        try {
            analytics.setAnalyticsCollectionEnabled(enabled)
        } catch (e: Exception) {
            YallaFirebase.logger.log("Analytics", "Failed to set collection enabled: ${e.message}")
        }
    }
}
