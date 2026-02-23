package uz.yalla.media.gallery

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

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
