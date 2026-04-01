package uz.yalla.media.gallery.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import app.cash.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import uz.yalla.media.gallery.model.YallaMediaImage
import uz.yalla.media.gallery.repository.YallaGalleryRepository

/**
 * ViewModel that exposes a paginated stream of device gallery images via Paging 3.
 *
 * Pages are 50 items each, cached in [viewModelScope] so that configuration changes
 * (e.g., screen rotation) do not trigger a reload.
 *
 * @param yallaGalleryRepository Repository providing the [PagingSource] for gallery images.
 * @since 0.0.1
 */
internal class YallaGalleryViewModel(private val yallaGalleryRepository: YallaGalleryRepository) : ViewModel() {
    /**
     * Flow of paginated gallery image data, cached in [viewModelScope].
     *
     * @since 0.0.1
     */
    val images: Flow<PagingData<YallaMediaImage>> =
        Pager(
            config =
                PagingConfig(
                    pageSize = 50,
                    initialLoadSize = 50,
                    enablePlaceholders = true
                )
        ) {
            yallaGalleryRepository.getPicturePagingSource()
        }.flow.cachedIn(viewModelScope)
}
