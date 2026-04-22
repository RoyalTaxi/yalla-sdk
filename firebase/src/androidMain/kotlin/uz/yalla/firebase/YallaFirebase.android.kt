package uz.yalla.firebase

import com.google.firebase.FirebaseApp

/**
 * Android-specific Firebase initialization.
 *
 * On Android, Firebase is auto-initialized by the `google-services` Gradle plugin, which
 * reads `google-services.json` at build time. This function validates that a [FirebaseApp]
 * instance exists before marking [YallaFirebase] as initialized.
 *
 * Fails loudly with a descriptive [IllegalStateException] if `google-services.json` is missing
 * or the `com.google.gms.google-services` Gradle plugin is not applied — both of which prevent
 * [FirebaseApp] from initializing.
 *
 * This function is called internally by [YallaFirebase.initialize] and should never be
 * invoked directly.
 *
 * @throws IllegalStateException if Firebase has not been initialized (missing `google-services.json`
 *   or `com.google.gms.google-services` plugin).
 * @see YallaFirebase.initialize
 * @see YallaFirebase.markInitialized
 * @since 0.0.1
 */
internal actual fun initializePlatform() {
    val firebaseApp = try {
        FirebaseApp.getInstance()
    } catch (e: IllegalStateException) {
        throw IllegalStateException(
            "YallaFirebase: Firebase is not initialized on Android. " +
                "Ensure `google-services.json` is present in your app module " +
                "and the `com.google.gms.google-services` Gradle plugin is applied. " +
                "Root cause: ${e.message}",
            e,
        )
    }
    requireNotNull(firebaseApp) {
        "YallaFirebase: FirebaseApp.getInstance() returned null. " +
            "This typically indicates a corrupted Gradle configuration."
    }
    YallaFirebase.markInitialized()
}
