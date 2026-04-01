package uz.yalla.media.camera

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.refTo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.darwin.NSObject
import platform.posix.memcpy
import uz.yalla.media.utils.getRootViewController

/**
 * iOS implementation of [rememberSystemCameraLauncher] using [UIImagePickerController].
 *
 * Presents the native camera UI via the topmost view controller obtained from
 * [getRootViewController]. If the camera source is unavailable, [onResult] receives `null`.
 *
 * @param scope    Coroutine scope used for launching the presentation and delivering results.
 * @param onResult Callback receiving the captured JPEG bytes, or `null` on cancellation/failure.
 * @return A [SystemCameraLauncher] instance remembered across recompositions.
 * @since 0.0.1
 */
@Composable
actual fun rememberSystemCameraLauncher(
    scope: CoroutineScope,
    onResult: (ByteArray?) -> Unit
): SystemCameraLauncher {
    val delegate = remember { SystemCameraDelegate(scope, onResult) }

    return remember {
        SystemCameraLauncher {
            scope.launch(Dispatchers.Main) {
                val cameraSource = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
                if (!UIImagePickerController.isSourceTypeAvailable(cameraSource)) {
                    onResult(null)
                    return@launch
                }

                val rootVC = getRootViewController() ?: return@launch
                val picker =
                    UIImagePickerController().apply {
                        sourceType = cameraSource
                        allowsEditing = false
                        this.delegate = delegate
                    }
                rootVC.presentViewController(picker, animated = true, completion = null)
            }
        }
    }
}

/**
 * iOS implementation of [SystemCameraLauncher].
 *
 * Directly delegates to the `onLaunch` closure without double-launch guarding
 * (the iOS UIKit presentation handles this natively).
 *
 * @param onLaunch Action that presents `UIImagePickerController`.
 * @since 0.0.1
 */
actual class SystemCameraLauncher actual constructor(private val onLaunch: () -> Unit) {
    /** @since 0.0.1 */
    actual fun launch() = onLaunch()
}

private class SystemCameraDelegate(
    private val scope: CoroutineScope,
    private val onResult: (ByteArray?) -> Unit
) : NSObject(),
    UIImagePickerControllerDelegateProtocol,
    UINavigationControllerDelegateProtocol {
    override fun imagePickerController(
        picker: UIImagePickerController,
        didFinishPickingMediaWithInfo: Map<Any?, *>
    ) {
        val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage
        val bytes = image?.toByteArray()

        picker.dismissViewControllerAnimated(flag = true, completion = null)
        scope.launch(Dispatchers.Main) {
            onResult(bytes)
        }
    }

    override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
        picker.dismissViewControllerAnimated(flag = true, completion = null)
        scope.launch(Dispatchers.Main) {
            onResult(null)
        }
    }
}

/**
 * Converts this [UIImage] to a JPEG-encoded [ByteArray] at full quality.
 *
 * @return JPEG bytes, or `null` when [UIImageJPEGRepresentation] fails (e.g. invalid image).
 * @since 0.0.1
 */
@OptIn(ExperimentalForeignApi::class)
private fun UIImage.toByteArray(): ByteArray? {
    val jpegData = UIImageJPEGRepresentation(this, 1.0) ?: return null
    val length = jpegData.length.toInt()
    if (length == 0) return null
    return ByteArray(length).apply {
        memcpy(this.refTo(0), jpegData.bytes, jpegData.length)
    }
}
