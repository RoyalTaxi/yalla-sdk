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
import platform.UIKit.UIImage
import platform.darwin.NSObject
import uz.yalla.media.utils.getRootViewController
import kotlin.coroutines.resume

/**
 * iOS implementation of [rememberImagePickerLauncher] using [PHPickerViewController].
 *
 * Presents the system photo picker via the topmost view controller obtained from
 * [getRootViewController]. Selected images are resized and filtered according to the
 * provided options before being delivered as byte arrays.
 *
 * @param selectionMode Single or multiple image selection.
 * @param scope         Coroutine scope for async image processing.
 * @param resizeOptions Target dimensions and compression quality.
 * @param filterOptions Color filter to apply to selected images.
 * @param onResult      Callback receiving the list of processed JPEG byte arrays.
 * @return An [ImagePickerLauncher] instance remembered across recompositions.
 * @since 0.0.1
 */
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

        // PHPicker delegate delivers a List<*>; the element type is ObjC-documented but un-generified.
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

/**
 * iOS implementation of [ImagePickerLauncher].
 *
 * Directly delegates to the `onLaunch` closure which presents a `PHPickerViewController`.
 *
 * @param selectionMode Single or multiple selection mode.
 * @param onLaunch Action that presents the PHPicker.
 * @since 0.0.1
 */
actual class ImagePickerLauncher actual constructor(
    selectionMode: SelectionMode,
    private val onLaunch: () -> Unit
) {
    /** @since 0.0.1 */
    actual fun launch() = onLaunch()
}
