package uz.yalla.firebase

/**
 * Android-specific Firebase initialization.
 * On Android, Firebase is auto-initialized via google-services.json plugin.
 */
internal actual fun initializePlatform() {
    // On Android, Firebase is auto-initialized via google-services.json
    // This is called to ensure the Firebase singleton is accessed
    YallaFirebase.markInitialized()
}
