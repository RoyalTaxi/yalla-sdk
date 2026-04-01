package uz.yalla.media.gallery.repository

import android.content.Context
import app.cash.paging.PagingSource
import uz.yalla.media.gallery.datasource.YallaGalleryDataSource
import uz.yalla.media.gallery.model.YallaMediaImage
import uz.yalla.media.gallery.util.createCursor
import uz.yalla.media.gallery.util.fetchPagePicture

/**
 * [YallaGalleryRepository] implementation backed by the Android [MediaStore].
 *
 * Delegates cursor creation and row mapping to the helper functions in
 * [uz.yalla.media.gallery.util] and wraps them in a Paging 3 [YallaGalleryDataSource].
 *
 * @param context Android context for content resolver access.
 * @since 0.0.1
 */
internal class YallaGalleryRepositoryImpl(private val context: Context) : YallaGalleryRepository {
    override suspend fun getCount(): Int {
        val cursor = context.createCursor(Int.MAX_VALUE, 0) ?: return 0
        val count = cursor.count
        cursor.close()
        return count
    }

    override suspend fun getByOffset(offset: Int): YallaMediaImage? = context.fetchPagePicture(1, offset).firstOrNull()

    override fun getPicturePagingSource(): PagingSource<Int, YallaMediaImage> =
        YallaGalleryDataSource { limit, offset -> context.fetchPagePicture(limit, offset) }
}
