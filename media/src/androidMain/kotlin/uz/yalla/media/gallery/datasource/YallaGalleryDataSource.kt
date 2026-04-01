package uz.yalla.media.gallery.datasource

import app.cash.paging.PagingSource
import app.cash.paging.PagingState
import uz.yalla.media.gallery.model.YallaMediaImage

/**
 * Paging 3 [PagingSource] that loads gallery images page-by-page from the device media store.
 *
 * Each page is fetched via the [onFetch] lambda which receives a `limit` and `offset`
 * and returns a list of [YallaMediaImage] items for that page.
 *
 * @param onFetch Lambda that loads a page of images given `(limit, offset)`.
 * @see YallaGalleryRepositoryImpl
 * @since 0.0.1
 */
internal class YallaGalleryDataSource(private val onFetch: (limit: Int, offset: Int) -> List<YallaMediaImage>) :
    PagingSource<Int, YallaMediaImage>() {
    override fun getRefreshKey(state: PagingState<Int, YallaMediaImage>): Int? =
        state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, YallaMediaImage> {
        val pageNumber = params.key ?: 0
        val pageSize = params.loadSize
        val pictures = onFetch.invoke(pageSize, pageNumber * pageSize)
        val prevKey = if (pageNumber > 0) pageNumber.minus(1) else null
        val nextKey = if (pictures.isNotEmpty()) pageNumber.plus(1) else null

        return LoadResult.Page(
            data = pictures,
            prevKey = prevKey,
            nextKey = nextKey
        )
    }
}
