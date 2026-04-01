package uz.yalla.firebase.crashlytics

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.crashlytics.FirebaseCrashlytics
import dev.gitlive.firebase.crashlytics.crashlytics
import uz.yalla.firebase.YallaFirebase
import uz.yalla.firebase.analytics.YallaAnalytics

/**
 * Wrapper for Firebase Crashlytics that provides safe crash reporting with built-in error handling.
 *
 * All operations are wrapped in `try-catch` blocks. Exceptions thrown by the underlying
 * Firebase SDK are swallowed and forwarded to [YallaFirebase.logger] — they do not propagate
 * to the caller. This ensures that crash-reporting failures never cause additional crashes.
 *
 * Access this class via [YallaFirebase.crashlytics]; do not instantiate it directly.
 *
 * @see YallaFirebase.crashlytics
 * @since 0.0.1
 */
class YallaCrashlytics {
    private val crashlytics: FirebaseCrashlytics by lazy { Firebase.crashlytics }

    /**
     * Logs a diagnostic message that appears in the next crash report.
     *
     * Logged messages are stored in a rolling buffer (max 64 KB) and included in the
     * Crashlytics report alongside any subsequent fatal or non-fatal exception. Messages
     * are not sent to Crashlytics until a crash or [recordException] call occurs.
     *
     * @param message The message to log.
     * @see recordException
     * @since 0.0.1
     */
    fun log(message: String) {
        try {
            crashlytics.log(message)
        } catch (e: Exception) {
            YallaFirebase.logger.log("Crashlytics", "Failed to log message: ${e.message}")
        }
    }

    /**
     * Records a non-fatal exception and sends it to Crashlytics.
     *
     * Use this for caught exceptions that represent unexpected states but don't crash
     * the app (e.g. network errors in critical paths, unexpected server responses).
     * The exception appears under "Non-fatals" in the Firebase console.
     *
     * @param throwable The exception to record.
     * @see log
     * @see setCustomKey
     * @since 0.0.1
     */
    fun recordException(throwable: Throwable) {
        try {
            crashlytics.recordException(throwable)
        } catch (e: Exception) {
            YallaFirebase.logger.log("Crashlytics", "Failed to record exception: ${e.message}")
        }
    }

    /**
     * Attaches a custom key-value pair to the next crash report.
     *
     * Custom keys appear in the Crashlytics console alongside the stack trace. Use them
     * to capture app state at the time of a crash (e.g. `"user_role"` to `"driver"`).
     * Firebase supports up to 64 custom key-value pairs per report.
     *
     * @param key The custom key name (max 1024 chars).
     * @param value The custom value (max 1024 chars).
     * @see recordException
     * @since 0.0.1
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
     * Sets a user identifier that is attached to crash reports.
     *
     * Helps identify which users are affected by a crash. Use an opaque ID (not PII
     * like email or name) to comply with privacy regulations.
     *
     * @param userId An opaque identifier for the current user.
     * @see setCustomKey
     * @since 0.0.1
     */
    fun setUserId(userId: String) {
        try {
            crashlytics.setUserId(userId)
        } catch (e: Exception) {
            YallaFirebase.logger.log("Crashlytics", "Failed to set user ID: ${e.message}")
        }
    }

    /**
     * Enables or disables Crashlytics data collection.
     *
     * Use this to honour user consent preferences (e.g. GDPR opt-out). When disabled,
     * no crash data is sent to Firebase. The setting persists across app restarts.
     *
     * @param enabled `true` to enable crash reporting, `false` to disable it.
     * @see YallaAnalytics.setAnalyticsCollectionEnabled
     * @since 0.0.1
     */
    fun setCrashlyticsCollectionEnabled(enabled: Boolean) {
        try {
            crashlytics.setCrashlyticsCollectionEnabled(enabled)
        } catch (e: Exception) {
            YallaFirebase.logger.log("Crashlytics", "Failed to set collection enabled: ${e.message}")
        }
    }
}
