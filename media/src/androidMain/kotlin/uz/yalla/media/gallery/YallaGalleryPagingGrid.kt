package uz.yalla.media.gallery

import android.Manifest
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.cash.paging.LoadStateLoading
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uz.yalla.media.gallery.model.YallaMediaImage
import uz.yalla.media.gallery.repository.YallaGalleryRepositoryImpl
import uz.yalla.media.gallery.viewmodel.YallaGalleryViewModel

/**
 * Android-only in-app gallery composable backed by a Paging 3 grid.
 *
 * Renders a paginated [LazyVerticalGrid] of device photos using Paging 3 and CashApp Paging.
 * Requests `READ_MEDIA_IMAGES` (API 33+) or `READ_EXTERNAL_STORAGE` permissions automatically
 * via Accompanist.
 *
 * This composable is **Android-only**. For a cross-platform single-image picker use
 * [YallaGallery] instead.
 *
 * @param modifier Layout modifier applied to the gallery container.
 * @param state Grid layout configuration (padding, spacing, columns, corner radius).
 * @param lazyGridState Scroll state for the underlying [LazyVerticalGrid].
 * @param backgroundColor Background color behind the grid.
 * @param header Optional composable rendered above the grid.
 * @param progressIndicator Composable shown while the initial page loads.
 * @param permissionDeniedContent Composable shown when storage permission is denied.
 * @param onImageSelected Callback invoked with the selected image bytes, or `null` on failure.
 * @since 0.0.1
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun YallaGalleryPagingGrid(
    modifier: Modifier = Modifier,
    state: GalleryPickerState = rememberGalleryPickerState(),
    lazyGridState: LazyGridState = rememberLazyGridState(),
    backgroundColor: Color = Color.Black,
    header: @Composable () -> Unit = {},
    progressIndicator: @Composable () -> Unit = {},
    permissionDeniedContent: @Composable () -> Unit = {},
    onImageSelected: (ByteArray?) -> Unit,
) {
    val context = LocalContext.current
    val storagePermission = getPagingGridStoragePermission()
    val permissionState = rememberPermissionState(storagePermission)
    var hasRequested by remember { mutableStateOf(false) }

    val viewModel =
        viewModel<YallaGalleryViewModel> {
            YallaGalleryViewModel(YallaGalleryRepositoryImpl(context))
        }

    val images = viewModel.images.collectAsLazyPagingItems()

    LaunchedEffect(permissionState.status) {
        if (!permissionState.status.isGranted && !hasRequested) {
            hasRequested = true
            permissionState.launchPermissionRequest()
        }
    }

    Box(modifier = modifier.background(backgroundColor)) {
        if (permissionState.status.isGranted) {
            PagingGalleryContent(state, lazyGridState, header, images, progressIndicator, onImageSelected)
        } else {
            permissionDeniedContent()
        }
    }
}

@Composable
private fun getPagingGridStoragePermission() =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

@Composable
private fun PagingGalleryContent(
    state: GalleryPickerState,
    lazyGridState: LazyGridState,
    header: @Composable () -> Unit,
    images: LazyPagingItems<YallaMediaImage>,
    progressIndicator: @Composable () -> Unit,
    onImageSelected: (ByteArray?) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val cellSizePx =
        remember(configuration, state, density) {
            with(density) {
                val totalSpacingDp = (state.contentPadding * 2 + state.itemSpacing * (state.columns - 1)).dp
                val availableWidth = configuration.screenWidthDp.dp - totalSpacingDp
                (availableWidth / state.columns).roundToPx().coerceAtLeast(1)
            }
        }

    Column(modifier = Modifier.background(Color.Transparent)) {
        header()

        if (images.loadState.refresh is LoadStateLoading && images.itemCount == 0) {
            progressIndicator()
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(state.columns),
                state = lazyGridState,
                contentPadding = PaddingValues(horizontal = state.contentPadding.dp),
                horizontalArrangement = Arrangement.spacedBy(state.itemSpacing.dp),
                verticalArrangement = Arrangement.spacedBy(state.itemSpacing.dp)
            ) {
                items(
                    count = images.itemCount,
                    key = { index -> images.peek(index)?.id ?: index }
                ) { index ->
                    val photo = images[index] ?: return@items
                    val thumbnail by produceState<android.graphics.Bitmap?>(
                        initialValue = null,
                        key1 = photo.id,
                        key2 = cellSizePx
                    ) {
                        value =
                            withContext(Dispatchers.IO) {
                                loadThumbnailBitmap(context, photo, cellSizePx)
                            }
                    }

                    PagingGalleryImageCard(
                        bitmap = thumbnail,
                        state = state,
                        onClick = {
                            scope.launch {
                                val bytes =
                                    withContext(Dispatchers.IO) {
                                        getOriginalImageByteArray(context, photo.uri)
                                    }
                                onImageSelected(bytes)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PagingGalleryImageCard(
    bitmap: android.graphics.Bitmap?,
    state: GalleryPickerState,
    onClick: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(state.cornerSize.dp),
        modifier = Modifier.aspectRatio(1f),
        onClick = onClick
    ) {
        if (bitmap == null) {
            Box(modifier = Modifier.fillMaxSize())
        } else {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.clip(RoundedCornerShape(state.cornerSize.dp))
            )
        }
    }
}
