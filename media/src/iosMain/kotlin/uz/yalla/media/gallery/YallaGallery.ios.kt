package uz.yalla.media.gallery

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
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
    onImageSelected: (ByteArray?) -> Unit
) {
    val imageFlow = remember { MutableSharedFlow<List<YallaMediaAsset>>(replay = 1) }
    val images by imageFlow.collectAsState(initial = emptyList())
    var isLoading by remember { mutableStateOf(true) }

    Box(modifier = modifier.background(backgroundColor)) {
        if (checkPhotoLibraryAuthorization()) {
            LaunchedEffect(Unit) {
                fetchImagesAsFlow().collectLatest { fetchedImages ->
                    imageFlow.emit(fetchedImages)
                    isLoading = false
                }
            }

            if (isLoading) {
                progressIndicator()
            } else {
                GalleryGrid(state, lazyGridState, header, images, onImageSelected)
            }
        } else {
            permissionDeniedContent()
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun GalleryGrid(
    state: GalleryPickerState,
    lazyGridState: LazyGridState,
    header: @Composable () -> Unit,
    images: List<YallaMediaAsset>,
    onImageSelected: (ByteArray?) -> Unit
) {
    val scope = rememberCoroutineScope()

    LazyVerticalGrid(
        columns = GridCells.Fixed(state.columns),
        state = lazyGridState,
        contentPadding = PaddingValues(horizontal = state.contentPadding.dp),
        horizontalArrangement = Arrangement.spacedBy(state.itemSpacing.dp),
        verticalArrangement = Arrangement.spacedBy(state.itemSpacing.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) { header() }

        items(images.size) { index ->
            val item = images[index]
            val imageByteArray = item.thumbnailBytes
            imageByteArray?.toImageBitmap()?.let { bitmap ->
                GalleryImageCard(
                    bitmap = bitmap,
                    state = state,
                    onClick = {
                        scope.launch {
                            onImageSelected(item.asset.getFullImageByteArray())
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun GalleryImageCard(
    bitmap: androidx.compose.ui.graphics.ImageBitmap,
    state: GalleryPickerState,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(state.cornerSize.dp),
        modifier = Modifier.aspectRatio(1f).clip(RoundedCornerShape(state.cornerSize.dp)),
        onClick = onClick
    ) {
        Image(
            bitmap = bitmap,
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
    }
}

private fun CoroutineScope.fetchImagesAsFlow() =
    flow {
        val images = fetchImagesFromGallery()
        emit(images)
    }.catch {
        emit(emptyList())
    }.flowOn(Dispatchers.Default)
