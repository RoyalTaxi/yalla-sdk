package uz.yalla.media.picker

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import uz.yalla.media.picker.SelectionMode.Companion.INFINITY

@Composable
actual fun rememberImagePickerLauncher(
    selectionMode: SelectionMode,
    scope: CoroutineScope,
    resizeOptions: ResizeOptions,
    filterOptions: FilterOptions,
    onResult: (List<ByteArray>) -> Unit
): ImagePickerLauncher =
    when (selectionMode) {
        SelectionMode.Single -> pickSingleImage(scope, selectionMode, resizeOptions, filterOptions, onResult)
        is SelectionMode.Multiple -> pickMultipleImages(scope, selectionMode, resizeOptions, filterOptions, onResult)
    }

@Composable
private fun pickSingleImage(
    scope: CoroutineScope,
    selectionMode: SelectionMode,
    resizeOptions: ResizeOptions,
    filterOptions: FilterOptions,
    onResult: (List<ByteArray>) -> Unit
): ImagePickerLauncher {
    val context = LocalContext.current
    var launcher: ImagePickerLauncher? = null

    val activityLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { uri ->
                uri?.let {
                    YallaImageResizer.resizeImageAsync(
                        context,
                        scope,
                        uri,
                        resizeOptions.width,
                        resizeOptions.height,
                        resizeOptions.resizeThresholdBytes,
                        resizeOptions.compressionQuality,
                        filterOptions
                    ) { resizedImage ->
                        resizedImage?.let { onResult(listOf(it)) }
                    }
                }
                launcher?.markPhotoPickerInactive()
            }
        )

    launcher =
        remember {
            ImagePickerLauncher(selectionMode) {
                activityLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }

    return launcher
}

@Composable
private fun pickMultipleImages(
    scope: CoroutineScope,
    selectionMode: SelectionMode.Multiple,
    resizeOptions: ResizeOptions,
    filterOptions: FilterOptions,
    onResult: (List<ByteArray>) -> Unit
): ImagePickerLauncher {
    val context = LocalContext.current
    var launcher: ImagePickerLauncher? = null
    val maxSelection = if (selectionMode.maxSelection == INFINITY) getMaxItems() else selectionMode.maxSelection

    val activityLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickMultipleVisualMedia(maxSelection),
            onResult = { uriList ->
                processMultipleImages(context, scope, uriList, resizeOptions, filterOptions, onResult)
                launcher?.markPhotoPickerInactive()
            }
        )

    launcher =
        remember {
            ImagePickerLauncher(selectionMode) {
                activityLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }

    return launcher
}

private fun processMultipleImages(
    context: android.content.Context,
    scope: CoroutineScope,
    uriList: List<android.net.Uri>,
    resizeOptions: ResizeOptions,
    filterOptions: FilterOptions,
    onResult: (List<ByteArray>) -> Unit
) {
    if (uriList.isEmpty()) {
        onResult(emptyList())
        return
    }

    val resizedImages = MutableList<ByteArray?>(uriList.size) { null }
    var remaining = uriList.size

    uriList.forEachIndexed { index, uri ->
        YallaImageResizer.resizeImageAsync(
            context,
            scope,
            uri,
            resizeOptions.width,
            resizeOptions.height,
            resizeOptions.resizeThresholdBytes,
            resizeOptions.compressionQuality,
            filterOptions
        ) { resizedImage ->
            resizedImages[index] = resizedImage
            remaining -= 1
            if (remaining == 0) {
                onResult(resizedImages.filterNotNull())
            }
        }
    }
}

actual class ImagePickerLauncher actual constructor(
    selectionMode: SelectionMode,
    private val onLaunch: () -> Unit
) {
    private var isPhotoPickerActive = false

    fun markPhotoPickerInactive() {
        isPhotoPickerActive = false
    }

    actual fun launch() {
        if (isPhotoPickerActive) return
        isPhotoPickerActive = true
        onLaunch()
    }
}
