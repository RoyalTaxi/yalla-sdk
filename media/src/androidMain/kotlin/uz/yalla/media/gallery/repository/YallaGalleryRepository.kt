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
 * @since 0.0.1
 */
internal interface YallaGalleryRepository {
    /**
     * Returns the total number of images in the device media store.
     *
     * @return Image count, or `0` if the query fails.
     * @since 0.0.1
     */
    suspend fun getCount(): Int

    /**
     * Retrieves a single image at the given [offset] in the date-sorted list.
     *
     * @param offset Zero-based position in the sorted image collection.
     * @return The image at [offset], or `null` if out of range.
     * @since 0.0.1
     */
    suspend fun getByOffset(offset: Int): YallaMediaImage?

    /**
     * Creates a fresh [PagingSource] for paginated gallery loading.
     *
     * @return A new [PagingSource] that fetches pages of [YallaMediaImage] items.
     * @since 0.0.1
     */
    fun getPicturePagingSource(): PagingSource<Int, YallaMediaImage>
}
