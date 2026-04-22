package uz.yalla.firebase

import uz.yalla.firebase.analytics.AnalyticsEvent
import uz.yalla.firebase.analytics.YallaAnalytics
import uz.yalla.firebase.crashlytics.YallaCrashlytics
import uz.yalla.firebase.logging.YallaFirebaseLogger
import uz.yalla.firebase.messaging.MessagingDelegate
import uz.yalla.firebase.messaging.YallaMessaging

/**
 * Main entry point for the Yalla Firebase SDK.
 *
 * [YallaFirebase] is a singleton that provides lazy access to all Firebase services:
 * [analytics], [crashlytics], and [messaging]. Each service is instantiated on first
 * access via Kotlin's `by lazy` delegation.
 *
 * **Initialization is required** before accessing any service. Call [initialize] once
 * at application startup. On iOS, `FirebaseApp.configure()` must be called in Swift
 * **before** [initialize] is invoked from Kotlin.
 *
 * Usage example:
 * ```kotlin
 * // Android — call from Application.onCreate()
 * YallaFirebase.initialize()
 *
 * // Optional: attach a custom logger
 * YallaFirebase.logger = MyFirebaseLogger()
 *
 * // Access services after initialization
 * YallaFirebase.analytics.log(AnalyticsEvent.ScreenView("Home"))
 * YallaFirebase.crashlytics.recordException(e)
 * ```
 *
 * @see YallaAnalytics
 * @see YallaCrashlytics
 * @see YallaMessaging
 * @see YallaFirebaseLogger
 * @since 0.0.1
 */
object YallaFirebase {
    /**
     * Pluggable logger for Firebase wrapper operations.
     *
     * Assign a custom [YallaFirebaseLogger] implementation to capture log output
     * (e.g., forward to Timber, NSLog, or a remote logging service).
     * Defaults to [YallaFirebaseLogger.Noop], which silently discards all messages.
     *
     * @see YallaFirebaseLogger
     * @see YallaFirebaseLogger.Noop
     * @since 0.0.1
     */
    var logger: YallaFirebaseLogger = YallaFirebaseLogger.Noop

    private var _isInitialized = false

    /**
     * Whether [initialize] has been successfully called.
     *
     * Check this before accessing any Firebase service if initialization is conditional.
     * Accessing [analytics], [crashlytics], or [messaging] without initialization
     * may throw an [IllegalStateException] (especially on iOS).
     *
     * @since 0.0.1
     */
    val isInitialized: Boolean get() = _isInitialized

    private val _analytics: YallaAnalytics by lazy { YallaAnalytics() }

    /**
     * Firebase Analytics service.
     *
     * Accessing this property before [initialize] has been called throws [IllegalStateException]
     * with a clear message. This guard replaces the previously silent crash that the underlying
     * Firebase SDK would produce for uninitialized access.
     *
     * @throws IllegalStateException if [initialize] has not been called.
     * @see YallaAnalytics
     * @see AnalyticsEvent
     * @since 0.0.1
     */
    val analytics: YallaAnalytics
        get() {
            check(isInitialized) {
                "YallaFirebase.initialize() must be called before accessing `analytics`."
            }
            return _analytics
        }

    private val _crashlytics: YallaCrashlytics by lazy { YallaCrashlytics() }

    /**
     * Firebase Crashlytics service.
     *
     * Accessing this property before [initialize] has been called throws [IllegalStateException]
     * with a clear message. This guard replaces the previously silent crash that the underlying
     * Firebase SDK would produce for uninitialized access.
     *
     * @throws IllegalStateException if [initialize] has not been called.
     * @see YallaCrashlytics
     * @since 0.0.1
     */
    val crashlytics: YallaCrashlytics
        get() {
            check(isInitialized) {
                "YallaFirebase.initialize() must be called before accessing `crashlytics`."
            }
            return _crashlytics
        }

    private val _messaging: YallaMessaging by lazy { YallaMessaging() }

    /**
     * Firebase Cloud Messaging service.
     *
     * Accessing this property before [initialize] has been called throws [IllegalStateException]
     * with a clear message. This guard replaces the previously silent crash that the underlying
     * Firebase SDK would produce for uninitialized access.
     *
     * @throws IllegalStateException if [initialize] has not been called.
     * @see YallaMessaging
     * @see MessagingDelegate
     * @since 0.0.1
     */
    val messaging: YallaMessaging
        get() {
            check(isInitialized) {
                "YallaFirebase.initialize() must be called before accessing `messaging`."
            }
            return _messaging
        }

    /**
     * Initializes Firebase for the current platform.
     *
     * Must be called once before any Firebase service is accessed. The underlying
     * platform behavior differs:
     * - **Android**: Firebase is auto-initialized via `google-services.json`. This call
     *   confirms readiness and sets [isInitialized].
     * - **iOS**: Requires `FirebaseApp.configure()` to be called in Swift first.
     *   This call validates that a Firebase app is configured and sets [isInitialized].
     *   Throws [IllegalStateException] if `FirebaseApp.configure()` was not called.
     *
     * @see isInitialized
     * @since 0.0.1
     */
    fun initialize() {
        initializePlatform()
    }

    /**
     * Marks the SDK as initialized. Called internally by platform-specific implementations
     * once they have confirmed Firebase is ready.
     *
     * @since 0.0.1
     */
    internal fun markInitialized() {
        _isInitialized = true
    }
}

/**
 * Platform-specific Firebase initialization logic.
 *
 * Implemented separately for each target platform:
 * - **Android**: No-op beyond marking initialized (Firebase auto-initializes via `google-services.json`).
 * - **iOS**: Validates that `FirebaseApp.configure()` was called in Swift and throws
 *   [IllegalStateException] if not.
 *
 * This function is called internally by [YallaFirebase.initialize] and should never be
 * invoked directly by consumers.
 *
 * @see YallaFirebase.initialize
 * @since 0.0.1
 */
internal expect fun initializePlatform()
