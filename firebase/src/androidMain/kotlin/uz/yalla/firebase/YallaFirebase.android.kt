package uz.yalla.firebase

/**
 * Android-specific Firebase initialization.
 *
 * On Android, Firebase is auto-initialized by the `google-services` Gradle plugin, which
 * reads `google-services.json` at build time. This function simply marks [YallaFirebase]
 * as initialized so that lazy service accessors ([YallaFirebase.analytics],
 * [YallaFirebase.crashlytics], [YallaFirebase.messaging]) can proceed safely.
 *
 * This function is called internally by [YallaFirebase.initialize] and should never be
 * invoked directly.
 *
 * @see YallaFirebase.initialize
 * @see YallaFirebase.markInitialized
 * @since 0.0.1
 */
internal actual fun initializePlatform() {
    // On Android, Firebase is auto-initialized via google-services.json
    // This is called to ensure the Firebase singleton is accessed
    YallaFirebase.markInitialized()
}
