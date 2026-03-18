package uz.yalla.media.picker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSData
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.UISceneActivationStateForegroundActive
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowScene
import platform.darwin.NSObject
import kotlin.coroutines.resume

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberImagePickerLauncher(
    selectionMode: SelectionMode,
    scope: CoroutineScope,
    resizeOptions: ResizeOptions,
    filterOptions: FilterOptions,
    onResult: (List<ByteArray>) -> Unit
): ImagePickerLauncher {
    val delegate = createPickerDelegate(scope, resizeOptions, filterOptions, onResult)

    return remember {
        ImagePickerLauncher(selectionMode) {
            scope.launch(Dispatchers.Main) {
                getRootViewController()?.takeIf { it.view?.window != null }?.let { rootVC ->
                    rootVC.presentViewController(
                        createPHPickerViewController(delegate, selectionMode),
                        animated = true,
                        completion = null
                    )
                }
            }
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

private fun createPickerDelegate(
    scope: CoroutineScope,
    resizeOptions: ResizeOptions,
    filterOptions: FilterOptions,
    onResult: (List<ByteArray>) -> Unit
) = object : NSObject(), PHPickerViewControllerDelegateProtocol {
    override fun picker(
        picker: PHPickerViewController,
        didFinishPicking: List<*>
    ) {
        picker.dismissViewControllerAnimated(flag = true, completion = null)

        @Suppress("UNCHECKED_CAST")
        val results = didFinishPicking as List<PHPickerResult>

        processPickerResults(results, scope, resizeOptions, filterOptions, onResult)
    }
}

private fun processPickerResults(
    results: List<PHPickerResult>,
    scope: CoroutineScope,
    resizeOptions: ResizeOptions,
    filterOptions: FilterOptions,
    onResult: (List<ByteArray>) -> Unit
) {
    scope.launch(Dispatchers.Main) {
        val imageData =
            results
                .map { result ->
                    async(Dispatchers.Default) {
                        val nsData =
                            suspendCancellableCoroutine<NSData?> { continuation ->
                                result.itemProvider.loadDataRepresentationForTypeIdentifier("public.image") { data, _ ->
                                    continuation.resume(data)
                                }
                            } ?: return@async null

                        UIImage
                            .imageWithData(nsData)
                            ?.fitInto(
                                resizeOptions.width,
                                resizeOptions.height,
                                resizeOptions.resizeThresholdBytes,
                                resizeOptions.compressionQuality,
                                filterOptions
                            )?.toByteArray(resizeOptions.compressionQuality)
                    }
                }.awaitAll()
                .filterNotNull()

        onResult(imageData)
    }
}

actual class ImagePickerLauncher actual constructor(
    selectionMode: SelectionMode,
    private val onLaunch: () -> Unit
) {
    actual fun launch() = onLaunch()
}
