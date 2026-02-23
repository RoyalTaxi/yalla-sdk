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
        val app = Firebase.app
        println("YallaFirebase: Initialized with app ${app.name}")
        YallaFirebase.markInitialized()
    } catch (e: Exception) {
        println("YallaFirebase: Firebase not configured. Call FirebaseApp.configure() in Swift first.")
        throw IllegalStateException("Firebase not configured on iOS", e)
    }
}
