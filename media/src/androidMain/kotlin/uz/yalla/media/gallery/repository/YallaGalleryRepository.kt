package uz.yalla.media.gallery.repository

import app.cash.paging.PagingSource
import uz.yalla.media.gallery.model.YallaMediaImage

internal interface YallaGalleryRepository {
    suspend fun getCount(): Int

    suspend fun getByOffset(offset: Int): YallaMediaImage?

    fun getPicturePagingSource(): PagingSource<Int, YallaMediaImage>
}
