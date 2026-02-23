package uz.yalla.firebase

import uz.yalla.firebase.analytics.YallaAnalytics
import uz.yalla.firebase.crashlytics.YallaCrashlytics
import uz.yalla.firebase.logging.YallaFirebaseLogger
import uz.yalla.firebase.messaging.YallaMessaging

/**
 * Main entry point for Yalla Firebase SDK.
 * Provides access to Analytics, Crashlytics, and Messaging services.
 */
object YallaFirebase {
    var logger: YallaFirebaseLogger = YallaFirebaseLogger.Noop

    private var _isInitialized = false
    val isInitialized: Boolean get() = _isInitialized

    val analytics: YallaAnalytics by lazy { YallaAnalytics() }
    val crashlytics: YallaCrashlytics by lazy { YallaCrashlytics() }
    val messaging: YallaMessaging by lazy { YallaMessaging() }

    /**
     * Initialize Firebase. Must be called before using any Firebase services.
     * On iOS, this should be called after FirebaseApp.configure() in Swift.
     */
    fun initialize() {
        initializePlatform()
    }

    internal fun markInitialized() {
        _isInitialized = true
    }
}

internal expect fun initializePlatform()
