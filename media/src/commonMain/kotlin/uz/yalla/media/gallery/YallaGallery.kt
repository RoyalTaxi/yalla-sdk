package uz.yalla.media.gallery

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * In-app gallery composable that displays a paginated grid of device photos.
 *
 * Handles storage/photo-library permissions automatically and calls [onImageSelected] with
 * the JPEG bytes of the chosen image. On Android this renders a [LazyVerticalGrid] backed by
 * Paging 3; on iOS it presents a [PHPickerViewController].
 *
 * ## Usage
 *
 * ```kotlin
 * @OptIn(ExperimentalYallaGalleryApi::class)
 * @Composable
 * fun ProfilePhotoScreen(onPhotoPicked: (ByteArray) -> Unit) {
 *     YallaGallery(
 *         modifier = Modifier.fillMaxSize(),
 *         onImageSelected = { bytes -> bytes?.let(onPhotoPicked) },
 *     )
 * }
 * ```
 *
 * @param modifier Layout modifier applied to the gallery container.
 * @param state Grid layout configuration (padding, spacing, columns, corner radius).
 * @param lazyGridState Scroll state for the underlying grid (Android only).
 * @param backgroundColor Background color behind the grid.
 * @param header Optional composable rendered above the grid.
 * @param progressIndicator Composable shown while the initial page loads.
 * @param permissionDeniedContent Composable shown when storage permission is denied.
 * @param onImageSelected Callback invoked with the selected image bytes, or `null` on cancellation.
 * @since 0.0.1
 */
@ExperimentalYallaGalleryApi
@Composable
expect fun YallaGallery(
    modifier: Modifier = Modifier,
    state: GalleryPickerState = rememberGalleryPickerState(),
    lazyGridState: LazyGridState = rememberLazyGridState(),
    backgroundColor: Color = Color.Black,
    header: @Composable () -> Unit = {},
    progressIndicator: @Composable () -> Unit = {},
    permissionDeniedContent: @Composable () -> Unit = {},
    onImageSelected: (ByteArray?) -> Unit
)
