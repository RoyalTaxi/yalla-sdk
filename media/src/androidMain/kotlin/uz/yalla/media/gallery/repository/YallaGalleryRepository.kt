package uz.yalla.media.gallery.repository

import app.cash.paging.PagingSource
import uz.yalla.media.gallery.model.YallaMediaImage

/**
 * Contract for accessing device gallery images.
 *
 * Implementations provide total image count, single-image lookup by offset, and
 * a Paging 3 [PagingSource] for paginated grid display.
 *
 * @see YallaGalleryRepositoryImpl
 */
internal interface YallaGalleryRepository {
    /**
     * Returns the total number of images in the device media store.
     *
     * @return Image count, or `0` if the query fails.
     */
    suspend fun getCount(): Int

    /**
     * Retrieves a single image at the given [offset] in the date-sorted list.
     *
     * @return The image at [offset], or `null` if out of range.
     */
    suspend fun getByOffset(offset: Int): YallaMediaImage?

    /**
     * Creates a fresh [PagingSource] for paginated gallery loading.
     *
     * @return A new [PagingSource] that fetches pages of [YallaMediaImage] items.
     */
    fun getPicturePagingSource(): PagingSource<Int, YallaMediaImage>
}
