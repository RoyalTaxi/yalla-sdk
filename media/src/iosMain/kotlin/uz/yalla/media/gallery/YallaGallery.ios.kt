package uz.yalla.media.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.refTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import platform.Photos.PHAuthorizationStatusAuthorized
import platform.Photos.PHAuthorizationStatusNotDetermined
import platform.Photos.PHPhotoLibrary
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

@ExperimentalYallaGalleryApi
@Composable
actual fun YallaGallery(
    modifier: Modifier,
    state: GalleryPickerState,
    lazyGridState: LazyGridState,
    backgroundColor: Color,
    header: @Composable () -> Unit,
    progressIndicator: @Composable () -> Unit,
    permissionDeniedContent: @Composable () -> Unit,
    onImageSelected: (ByteArray?) -> Unit,
) {
    var isAuthorized by remember { mutableStateOf<Boolean?>(null) }
    var hasLaunched by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isAuthorized = checkOrRequestPhotoLibraryAccess()
    }

    Box(modifier = modifier.background(backgroundColor)) {
        when (isAuthorized) {
            null -> progressIndicator()
            false -> permissionDeniedContent()
            true -> {
                header()

                if (!hasLaunched) {
                    hasLaunched = true
                    LaunchedEffect(Unit) {
                        val result = launchPHPicker()
                        onImageSelected(result)
                    }
                }
            }
        }
    }
}

private suspend fun checkOrRequestPhotoLibraryAccess(): Boolean {
    val currentStatus = PHPhotoLibrary.authorizationStatus()
    if (currentStatus == PHAuthorizationStatusAuthorized) return true
    if (currentStatus != PHAuthorizationStatusNotDetermined) return false

    return suspendCancellableCoroutine { continuation ->
        PHPhotoLibrary.requestAuthorization { newStatus ->
            continuation.resume(newStatus == PHAuthorizationStatusAuthorized)
        }
    }
}

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
