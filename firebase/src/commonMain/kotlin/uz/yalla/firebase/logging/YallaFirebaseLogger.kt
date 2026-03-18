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
 * @since 0.0.1
 */
fun interface YallaFirebaseLogger {
    /**
     * Receives a log message from the Firebase wrapper.
     *
     * @param tag The source component (e.g. `"Analytics"`, `"Crashlytics"`, `"Messaging"`).
     * @param message A human-readable description of the event or error.
     * @since 0.0.1
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
         *
         * @since 0.0.1
         */
        val Noop = YallaFirebaseLogger { _, _ -> }
    }
}
