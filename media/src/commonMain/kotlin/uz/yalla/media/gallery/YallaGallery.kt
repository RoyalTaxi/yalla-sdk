package uz.yalla.media.gallery

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Cross-platform image-picker composable backed by the native system picker on each platform.
 *
 * On Android this triggers `ActivityResultContracts.PickVisualMedia` (the Android system
 * photo picker, introduced in API 19+). On iOS this presents a `PHPickerViewController`.
 * Both paths handle permissions automatically and return JPEG bytes of the selected image.
 *
 * This is the narrow, cross-platform surface of the gallery API. For Android-only rich
 * features (Paging 3 in-app grid, scroll state, custom header/footer, permission denied UI)
 * use `YallaGalleryPagingGrid` from `androidMain`.
 *
 * ## Usage
 *
 * ```kotlin
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
 * @param onImageSelected Callback invoked with the selected image bytes, or `null` on cancellation.
 * @since 0.0.1
 */
@Composable
expect fun YallaGallery(
    modifier: Modifier = Modifier,
    onImageSelected: (ByteArray?) -> Unit,
)
