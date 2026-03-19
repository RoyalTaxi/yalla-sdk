package uz.yalla.media.camera

/**
 * Represents the physical camera lens to use.
 *
 * Pass an instance to [YallaCamera] or [rememberYallaCameraState] to select which
 * device camera is active. Use [Front] for selfie/face-facing and [Back] for the
 * rear/world-facing lens.
 *
 * @since 0.0.1
 */
sealed class CameraMode {
    /** Front-facing (selfie) camera. @since 0.0.1 */
    data object Front : CameraMode()

    /** Rear-facing (world) camera. @since 0.0.1 */
    data object Back : CameraMode()
}

internal fun CameraMode.inverse(): CameraMode =
    when (this) {
        CameraMode.Back -> CameraMode.Front
        CameraMode.Front -> CameraMode.Back
    }

internal fun CameraMode.toId(): Int =
    when (this) {
        CameraMode.Back -> BACK_CAMERA_ID
        CameraMode.Front -> FRONT_CAMERA_ID
    }

internal fun cameraModeFromId(id: Int): CameraMode =
    when (id) {
        BACK_CAMERA_ID -> CameraMode.Back
        FRONT_CAMERA_ID -> CameraMode.Front
        else -> throw IllegalArgumentException("Unknown camera mode id: $id")
    }

private const val BACK_CAMERA_ID = 0
private const val FRONT_CAMERA_ID = 1
