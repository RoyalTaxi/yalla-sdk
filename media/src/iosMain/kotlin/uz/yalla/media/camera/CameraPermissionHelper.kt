package uz.yalla.media.camera

import platform.AVFoundation.AVAuthorizationStatusAuthorized
import platform.AVFoundation.AVAuthorizationStatusDenied
import platform.AVFoundation.AVAuthorizationStatusNotDetermined
import platform.AVFoundation.AVAuthorizationStatusRestricted
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.authorizationStatusForMediaType
import platform.AVFoundation.requestAccessForMediaType

/**
 * Checks the AVFoundation camera authorization status and delivers the result via [onResult].
 *
 * If the status is [AVAuthorizationStatusNotDetermined], a permission request dialog is
 * presented to the user. The callback fires on whichever thread the OS delivers the result
 * (typically the main thread for the undetermined case).
 *
 * @param onResult Callback receiving the resolved [CameraAccess] state.
 * @since 0.0.1
 */
internal fun checkCameraPermission(onResult: (CameraAccess) -> Unit) {
    when (AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)) {
        AVAuthorizationStatusAuthorized -> onResult(CameraAccess.Authorized)
        AVAuthorizationStatusDenied, AVAuthorizationStatusRestricted -> onResult(CameraAccess.Denied)
        AVAuthorizationStatusNotDetermined -> {
            AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { success ->
                onResult(if (success) CameraAccess.Authorized else CameraAccess.Denied)
            }
        }
    }
}
