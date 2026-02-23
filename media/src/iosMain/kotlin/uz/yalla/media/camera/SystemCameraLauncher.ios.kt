package uz.yalla.media.camera

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.refTo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.UIKit.UISceneActivationStateForegroundActive
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowScene
import platform.darwin.NSObject
import platform.posix.memcpy

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

actual class SystemCameraLauncher actual constructor(
    private val onLaunch: () -> Unit
) {
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

private fun getRootViewController(): UIViewController? {
    @Suppress("DEPRECATION")
    val legacyKeyWindow = UIApplication.sharedApplication.keyWindow

    val keyWindow =
        UIApplication.sharedApplication.connectedScenes
            .filterIsInstance<UIWindowScene>()
            .firstOrNull { it.activationState == UISceneActivationStateForegroundActive }
            ?.windows
            ?.filterIsInstance<UIWindow>()
            ?.firstOrNull { it.isKeyWindow() }
            ?: legacyKeyWindow

    var rootVC = keyWindow?.rootViewController()

    while (true) {
        val presented = rootVC?.presentedViewController() ?: break
        if (presented.view?.window != null && !presented.isBeingDismissed()) {
            rootVC = presented
        } else {
            break
        }
    }

    return rootVC?.takeIf { it.view?.window != null && !it.isBeingDismissed() }
        ?: keyWindow?.rootViewController()
}

@OptIn(ExperimentalForeignApi::class)
private fun UIImage.toByteArray(): ByteArray? {
    val jpegData = UIImageJPEGRepresentation(this, 1.0) ?: return null
    return ByteArray(jpegData.length.toInt()).apply {
        memcpy(this.refTo(0), jpegData.bytes, jpegData.length)
    }
}
