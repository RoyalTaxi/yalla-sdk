package uz.yalla.media.picker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_group_create
import platform.darwin.dispatch_group_enter
import platform.darwin.dispatch_group_leave
import platform.darwin.dispatch_group_notify

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
    val dispatchGroup = dispatch_group_create()
    val imageData = mutableListOf<ByteArray>()

    results.forEach { result ->
        dispatch_group_enter(dispatchGroup)
        result.itemProvider.loadDataRepresentationForTypeIdentifier("public.image") { nsData, _ ->
            scope.launch(Dispatchers.Main) {
                nsData?.let { data ->
                    UIImage
                        .imageWithData(data)
                        ?.fitInto(
                            resizeOptions.width,
                            resizeOptions.height,
                            resizeOptions.resizeThresholdBytes,
                            resizeOptions.compressionQuality,
                            filterOptions
                        )?.toByteArray(resizeOptions.compressionQuality)
                        ?.let { imageData.add(it) }
                }
                dispatch_group_leave(dispatchGroup)
            }
        }
    }

    dispatch_group_notify(dispatchGroup, dispatch_get_main_queue()) {
        scope.launch(Dispatchers.Main) {
            onResult(imageData)
        }
    }
}

actual class ImagePickerLauncher actual constructor(
    selectionMode: SelectionMode,
    private val onLaunch: () -> Unit
) {
    actual fun launch() = onLaunch()
}
