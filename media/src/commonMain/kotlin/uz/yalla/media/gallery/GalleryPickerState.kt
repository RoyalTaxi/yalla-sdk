package uz.yalla.media.gallery

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable

private const val DEFAULT_CONTENT_PADDING = 4
private const val DEFAULT_ITEM_SPACING = 4
private const val DEFAULT_CORNER_SIZE = 0
private const val DEFAULT_COLUMNS = 3

@Composable
fun rememberGalleryPickerState(
    contentPadding: Int = DEFAULT_CONTENT_PADDING,
    itemSpacing: Int = DEFAULT_ITEM_SPACING,
    cornerSize: Int = DEFAULT_CORNER_SIZE,
    columns: Int = DEFAULT_COLUMNS
): GalleryPickerState =
    rememberSaveable(saver = GalleryPickerState.Saver) {
        GalleryPickerState(
            contentPadding = contentPadding,
            itemSpacing = itemSpacing,
            cornerSize = cornerSize,
            columns = columns
        )
    }

@Stable
class GalleryPickerState(
    val contentPadding: Int,
    val itemSpacing: Int,
    val cornerSize: Int,
    val columns: Int
) {
    internal companion object {
        val Saver: Saver<GalleryPickerState, *> =
            listSaver(
                save = {
                    listOf<Any>(
                        it.contentPadding,
                        it.itemSpacing,
                        it.cornerSize,
                        it.columns
                    )
                },
                restore = {
                    GalleryPickerState(
                        contentPadding = it[0] as Int,
                        itemSpacing = it[1] as Int,
                        cornerSize = it[2] as Int,
                        columns = it[3] as Int
                    )
                }
            )
    }
}
