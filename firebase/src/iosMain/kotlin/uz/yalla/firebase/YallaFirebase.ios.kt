package uz.yalla.firebase

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.app

/**
 * iOS-specific Firebase initialization.
 * On iOS, FirebaseApp.configure() must be called in Swift before this.
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
