package uz.yalla.firebase

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.app

/**
 * iOS-specific Firebase initialization.
 *
 * On iOS, the Firebase SDK is **not** auto-initialized. The host application must call
 * `FirebaseApp.configure()` in Swift (typically in `AppDelegate.application(_:didFinishLaunchingWithOptions:)`)
 * **before** [YallaFirebase.initialize] is invoked from Kotlin.
 *
 * This function validates that a [Firebase] app instance exists. If `FirebaseApp.configure()`
 * was not called beforehand, an [IllegalStateException] is thrown with a descriptive message.
 * On success, [YallaFirebase] is marked as initialized so that lazy service accessors
 * ([YallaFirebase.analytics], [YallaFirebase.crashlytics], [YallaFirebase.messaging])
 * can proceed safely.
 *
 * This function is called internally by [YallaFirebase.initialize] and should never be
 * invoked directly.
 *
 * @throws IllegalStateException if `FirebaseApp.configure()` was not called in Swift first.
 * @see YallaFirebase.initialize
 * @see YallaFirebase.markInitialized
 * @since 0.0.1
 */
internal actual fun initializePlatform() {
    // On iOS, FirebaseApp.configure() must be called in Swift before this
    // This ensures the Kotlin SDK can access the configured Firebase instance
    try {
        Firebase.app
        YallaFirebase.markInitialized()
    } catch (e: Exception) {
        throw IllegalStateException("Firebase not configured on iOS", e)
    }
}
