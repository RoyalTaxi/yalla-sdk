package uz.yalla.media.gallery.datasource

import app.cash.paging.PagingSource
import app.cash.paging.PagingState
import uz.yalla.media.gallery.model.YallaMediaImage

internal class YallaGalleryDataSource(
    private val onFetch: (limit: Int, offset: Int) -> List<YallaMediaImage>
) : PagingSource<Int, YallaMediaImage>() {
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
