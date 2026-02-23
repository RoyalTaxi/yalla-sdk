package uz.yalla.firebase.crashlytics

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.crashlytics.FirebaseCrashlytics
import dev.gitlive.firebase.crashlytics.crashlytics
import uz.yalla.firebase.YallaFirebase

/**
 * Wrapper for Firebase Crashlytics with common crash reporting operations.
 */
class YallaCrashlytics {
    private val crashlytics: FirebaseCrashlytics by lazy { Firebase.crashlytics }

    /**
     * Log a message to Crashlytics for debugging.
     */
    fun log(message: String) {
        try {
            crashlytics.log(message)
        } catch (e: Exception) {
            YallaFirebase.logger.log("Crashlytics", "Failed to log message: ${e.message}")
        }
    }

    /**
     * Record a non-fatal exception.
     */
    fun recordException(throwable: Throwable) {
        try {
            crashlytics.recordException(throwable)
        } catch (e: Exception) {
            YallaFirebase.logger.log("Crashlytics", "Failed to record exception: ${e.message}")
        }
    }

    /**
     * Set a custom key-value pair for crash reports.
     */
    fun setCustomKey(
        key: String,
        value: String
    ) {
        try {
            crashlytics.setCustomKey(key, value)
        } catch (e: Exception) {
            YallaFirebase.logger.log("Crashlytics", "Failed to set custom key: ${e.message}")
        }
    }

    /**
     * Set the user identifier for crash reports.
     */
    fun setUserId(userId: String) {
        try {
            crashlytics.setUserId(userId)
        } catch (e: Exception) {
            YallaFirebase.logger.log("Crashlytics", "Failed to set user ID: ${e.message}")
        }
    }

    /**
     * Enable or disable Crashlytics collection.
     */
    fun setCrashlyticsCollectionEnabled(enabled: Boolean) {
        try {
            crashlytics.setCrashlyticsCollectionEnabled(enabled)
        } catch (e: Exception) {
            YallaFirebase.logger.log("Crashlytics", "Failed to set collection enabled: ${e.message}")
        }
    }
}
