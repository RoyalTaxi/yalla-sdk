package uz.yalla.media.camera

/**
 * Represents the current camera authorization state on iOS.
 *
 * Used internally by the camera composables to branch between showing the live preview,
 * showing a permission-denied fallback, or waiting for the user's response.
 *
 * @since 0.0.1
 */
internal sealed interface CameraAccess {
    /** Authorization status has not been determined yet. @since 0.0.1 */
    data object Undefined : CameraAccess

    /** The user denied or the system restricted camera access. @since 0.0.1 */
    data object Denied : CameraAccess

    /** Camera access has been granted. @since 0.0.1 */
    data object Authorized : CameraAccess
}
