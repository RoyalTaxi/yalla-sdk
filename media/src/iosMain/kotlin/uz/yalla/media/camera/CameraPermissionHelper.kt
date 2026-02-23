package uz.yalla.media.camera

import platform.AVFoundation.AVAuthorizationStatusAuthorized
import platform.AVFoundation.AVAuthorizationStatusDenied
import platform.AVFoundation.AVAuthorizationStatusNotDetermined
import platform.AVFoundation.AVAuthorizationStatusRestricted
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.authorizationStatusForMediaType
import platform.AVFoundation.requestAccessForMediaType

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
