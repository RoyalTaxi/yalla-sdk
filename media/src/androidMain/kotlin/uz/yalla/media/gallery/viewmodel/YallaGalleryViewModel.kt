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

internal class YallaGalleryViewModel(
    private val yallaGalleryRepository: YallaGalleryRepository
) : ViewModel() {
    fun getImages(): Flow<PagingData<YallaMediaImage>> =
        Pager(
            config =
                PagingConfig(
                    pageSize = 10,
                    initialLoadSize = 10,
                    enablePlaceholders = true
                )
        ) {
            yallaGalleryRepository.getPicturePagingSource()
        }.flow.cachedIn(viewModelScope)
}
