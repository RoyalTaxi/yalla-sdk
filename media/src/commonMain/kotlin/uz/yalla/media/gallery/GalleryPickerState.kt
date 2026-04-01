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

/**
 * Creates and remembers a [GalleryPickerState] that survives configuration changes.
 *
 * @param contentPadding Horizontal padding around the grid in dp. Defaults to 4.
 * @param itemSpacing Spacing between grid items in dp. Defaults to 4.
 * @param cornerSize Corner radius of each thumbnail card in dp. Defaults to 0 (sharp corners).
 * @param columns Number of grid columns. Defaults to 3.
 * @return A saveable [GalleryPickerState] instance.
 * @since 0.0.1
 */
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

/**
 * Layout configuration for [YallaGallery].
 *
 * Controls the grid appearance — padding, spacing, corner radius, and column count.
 * Create instances via [rememberGalleryPickerState] to automatically survive
 * configuration changes.
 *
 * @property contentPadding Horizontal padding around the grid in dp.
 * @property itemSpacing Spacing between grid items in dp.
 * @property cornerSize Corner radius of each thumbnail card in dp.
 * @property columns Number of grid columns.
 * @since 0.0.1
 */
@Stable
class GalleryPickerState(
    val contentPadding: Int,
    val itemSpacing: Int,
    val cornerSize: Int,
    val columns: Int
) {
    internal companion object {
        /**
         * [Saver] that serializes the four layout properties as a list of integers,
         * enabling [rememberSaveable] to persist the state across configuration changes.
         *
         * @since 0.0.1
         */
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
