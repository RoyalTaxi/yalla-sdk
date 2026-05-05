package uz.yalla.media.picker

import androidx.annotation.FloatRange
import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope

private const val DEFAULT_RESIZE_IMAGE_WIDTH = 800
private const val DEFAULT_RESIZE_IMAGE_HEIGHT = 800
private const val DEFAULT_RESIZE_THRESHOLD_BYTES = 1048576L

/**
 * Creates and remembers an [ImagePickerLauncher] for selecting images from the device gallery.
 *
 * On Android this uses the Photo Picker (`PickVisualMedia`); on iOS it presents a
 * `PHPickerViewController`. Selected images are optionally resized and filtered before
 * being delivered as byte arrays.
 *
 * ## Usage
 *
 * ```kotlin
 * val scope = rememberCoroutineScope()
 * val picker = rememberImagePickerLauncher(
 *     selectionMode = SelectionMode.Multiple(maxSelection = 5),
 *     scope = scope,
 *     resizeOptions = ResizeOptions(width = 1024, height = 1024),
 *     onResult = { images -> viewModel.uploadImages(images) },
 * )
 *
 * Button(onClick = { picker.launch() }) { Text("Pick Photos") }
 * ```
 *
 * @param selectionMode Single or multiple image selection. Defaults to [SelectionMode.Single].
 * @return A remembered [ImagePickerLauncher] instance.
 */
@Composable
expect fun rememberImagePickerLauncher(
    selectionMode: SelectionMode = SelectionMode.Single,
    scope: CoroutineScope,
    resizeOptions: ResizeOptions = ResizeOptions(),
    filterOptions: FilterOptions = FilterOptions.Default,
    onResult: (List<ByteArray>) -> Unit
): ImagePickerLauncher

/**
 * Controls how many images can be selected from the picker.
 */
sealed class SelectionMode {
    /** Allows selecting exactly one image. @since 0.0.1 */
    data object Single : SelectionMode()

    /**
     * Allows selecting multiple images.
     *
     * @property maxSelection Maximum number of selectable images. Use [INFINITY] (0) for no limit.
     */
    data class Multiple(
        val maxSelection: Int = INFINITY
    ) : SelectionMode()

    companion object {
        /** Sentinel value meaning no upper bound on selection count. @since 0.0.1 */
        const val INFINITY = 0
    }
}

/**
 * Options for automatic image resizing after selection.
 *
 * Images whose byte size exceeds [resizeThresholdBytes] are scaled down to fit within
 * [width] x [height] bounds. Smaller images are returned unmodified.
 *
 * @property width Maximum output width in pixels. Defaults to 800.
 * @property height Maximum output height in pixels. Defaults to 800.
 * @property resizeThresholdBytes Byte-size threshold below which resizing is skipped. Defaults to 1 MB.
 * @property compressionQuality JPEG compression quality (0.0 .. 1.0). Defaults to 1.0 (lossless).
 */
data class ResizeOptions(
    val width: Int = DEFAULT_RESIZE_IMAGE_WIDTH,
    val height: Int = DEFAULT_RESIZE_IMAGE_HEIGHT,
    val resizeThresholdBytes: Long = DEFAULT_RESIZE_THRESHOLD_BYTES,
    @FloatRange(from = 0.0, to = 1.0)
    val compressionQuality: Double = 1.0
)

/**
 * Color filter applied to images after selection.
 */
sealed interface FilterOptions {
    /** No filter applied. @since 0.0.1 */
    data object Default : FilterOptions

    /** Desaturates the image to grayscale. @since 0.0.1 */
    data object GrayScale : FilterOptions

    /** Applies a warm sepia tone. @since 0.0.1 */
    data object Sepia : FilterOptions

    /** Inverts all colors. @since 0.0.1 */
    data object Invert : FilterOptions
}

/**
 * Launcher for the system image picker.
 *
 * Obtain via [rememberImagePickerLauncher]. Call [launch] to present the picker;
 * results are delivered through the callback provided at creation time.
 */
expect class ImagePickerLauncher(
    selectionMode: SelectionMode,
    onLaunch: () -> Unit
) {
    /**
     * Opens the system image picker. On Android, guards against double-launch.
     */
    fun launch()
}
