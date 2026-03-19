package uz.yalla.media.gallery

import kotlin.test.Test
import kotlin.test.assertEquals

class GalleryPickerStateTest {

    @Test
    fun shouldHaveDefaultColumns3() {
        val state = GalleryPickerState(
            contentPadding = 4,
            itemSpacing = 4,
            cornerSize = 0,
            columns = 3,
        )
        assertEquals(3, state.columns)
    }

    @Test
    fun shouldHaveDefaultContentPadding4() {
        val state = GalleryPickerState(
            contentPadding = 4,
            itemSpacing = 4,
            cornerSize = 0,
            columns = 3,
        )
        assertEquals(4, state.contentPadding)
    }

    @Test
    fun shouldHaveDefaultItemSpacing4() {
        val state = GalleryPickerState(
            contentPadding = 4,
            itemSpacing = 4,
            cornerSize = 0,
            columns = 3,
        )
        assertEquals(4, state.itemSpacing)
    }

    @Test
    fun shouldHaveDefaultCornerSize0() {
        val state = GalleryPickerState(
            contentPadding = 4,
            itemSpacing = 4,
            cornerSize = 0,
            columns = 3,
        )
        assertEquals(0, state.cornerSize)
    }

    @Test
    fun shouldPreserveCustomValues() {
        val state = GalleryPickerState(
            contentPadding = 16,
            itemSpacing = 8,
            cornerSize = 12,
            columns = 4,
        )
        assertEquals(16, state.contentPadding)
        assertEquals(8, state.itemSpacing)
        assertEquals(12, state.cornerSize)
        assertEquals(4, state.columns)
    }

    @Test
    fun shouldPreserveZeroValues() {
        val state = GalleryPickerState(
            contentPadding = 0,
            itemSpacing = 0,
            cornerSize = 0,
            columns = 1,
        )
        assertEquals(0, state.contentPadding)
        assertEquals(0, state.itemSpacing)
        assertEquals(0, state.cornerSize)
        assertEquals(1, state.columns)
    }
}
