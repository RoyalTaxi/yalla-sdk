package uz.yalla.media.gallery

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.refTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerConfigurationSelectionOrdered
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UISceneActivationStateForegroundActive
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowScene
import platform.darwin.NSObject
import platform.posix.memcpy
import kotlin.coroutines.resume

/**
 * iOS implementation of [YallaGallery].
 *
 * Presents a `PHPickerViewController` for single-image selection.
 * The selected image is returned as JPEG bytes via [onImageSelected].
 * `PHPicker` handles photo-library permission natively — no explicit permission
 * request is made before presenting the picker.
 *
 * @since 0.0.1
 */
@Composable
actual fun YallaGallery(
    modifier: Modifier,
    onImageSelected: (ByteArray?) -> Unit,
) {
    var hasLaunched by remember { mutableStateOf(false) }

    if (!hasLaunched) {
        hasLaunched = true
        LaunchedEffect(Unit) {
            val result = launchPHPicker()
            onImageSelected(result)
        }
    }
}

/**
 * Presents a single-selection [PHPickerViewController] and suspends until the user picks an
 * image or cancels.
 *
 * The selected image is decoded, re-encoded as JPEG at 90 % quality, and returned as a
 * byte array. Returns `null` on cancellation or if any step in the pipeline fails.
 *
 * @return JPEG-encoded bytes of the selected image, or `null`.
 */
@OptIn(ExperimentalForeignApi::class)
private suspend fun launchPHPicker(): ByteArray? =
    withContext(Dispatchers.Main) {
        suspendCancellableCoroutine { continuation ->
            val config =
                PHPickerConfiguration().apply {
                    setSelectionLimit(1)
                    setFilter(PHPickerFilter.imagesFilter)
                    setSelection(PHPickerConfigurationSelectionOrdered)
                }

            val delegate =
                object : NSObject(), PHPickerViewControllerDelegateProtocol {
                    override fun picker(
                        picker: PHPickerViewController,
                        didFinishPicking: List<*>,
                    ) {
                        picker.dismissViewControllerAnimated(true, null)

                        // PHPicker delegate delivers a List<*>; the element type is ObjC-documented but un-generified.
                        @Suppress("UNCHECKED_CAST")
                        val results = didFinishPicking as List<PHPickerResult>
                        val result = results.firstOrNull()

                        if (result == null) {
                            continuation.resume(null)
                            return
                        }

                        result.itemProvider.loadDataRepresentationForTypeIdentifier(
                            "public.image",
                        ) { nsData, error ->
                            if (error != null || nsData == null) {
                                continuation.resume(null)
                                return@loadDataRepresentationForTypeIdentifier
                            }

                            val image = UIImage.imageWithData(nsData)
                            if (image == null) {
                                continuation.resume(null)
                                return@loadDataRepresentationForTypeIdentifier
                            }

                            val jpegData = UIImageJPEGRepresentation(image, 0.9)
                            if (jpegData == null) {
                                continuation.resume(null)
                                return@loadDataRepresentationForTypeIdentifier
                            }

                            val bytes =
                                ByteArray(jpegData.length.toInt()).apply {
                                    memcpy(this.refTo(0), jpegData.bytes, jpegData.length)
                                }
                            continuation.resume(bytes)
                        }
                    }
                }

            val picker =
                PHPickerViewController(configuration = config).apply {
                    this.delegate = delegate
                }

            getRootViewController()?.presentViewController(picker, animated = true, completion = null)
                ?: continuation.resume(null)
        }
    }

/**
 * Resolves the topmost [UIViewController] that is safe to present from.
 *
 * Walks the `presentedViewController` chain starting from the key window's root controller.
 *
 * @return The topmost presentable controller, or `null` if none is available.
 */
@Suppress("LoopWithTooManyJumpStatements") // walk up presentedViewController chain; each jump is a bounded step
private fun getRootViewController(): UIViewController? {
    // UIApplication.keyWindow is deprecated in iOS 13 but retained as a fallback when no UIWindowScene is active.
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
