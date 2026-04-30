package uz.yalla.firebase.logging

/**
 * Pluggable logging interface for Yalla Firebase wrapper operations.
 *
 * Implement this fun interface to capture log output from [uz.yalla.firebase.YallaFirebase]
 * and its service wrappers (Analytics, Crashlytics, Messaging). Logs are emitted when a
 * Firebase SDK call fails — useful for debugging without crashing the app.
 *
 * Assign a custom implementation to [uz.yalla.firebase.YallaFirebase.logger]:
 * ```kotlin
 * YallaFirebase.logger = YallaFirebaseLogger { tag, message ->
 *     Timber.tag(tag).w(message)
 * }
 * ```
 *
 * @see uz.yalla.firebase.YallaFirebase.logger
 */
fun interface YallaFirebaseLogger {
    /**
     * Receives a log message from the Firebase wrapper.
     *
     * @param tag The source component (e.g. `"Analytics"`, `"Crashlytics"`, `"Messaging"`).
     */
    fun log(
        tag: String,
        message: String
    )

    companion object {
        /**
         * Default no-op logger implementation.
         *
         * Silently discards all log messages. Used as the default value for
         * [uz.yalla.firebase.YallaFirebase.logger] so that no logging infrastructure
         * is required unless the caller opts in.
         */
        val Noop = YallaFirebaseLogger { _, _ -> }
    }
}
