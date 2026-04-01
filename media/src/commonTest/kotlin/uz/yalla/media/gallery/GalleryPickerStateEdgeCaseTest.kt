package uz.yalla.media.gallery

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame

class GalleryPickerStateEdgeCaseTest {
    // --- Large values ---

    @Test
    fun shouldPreserveLargeContentPadding() {
        val state =
            GalleryPickerState(
                contentPadding = Int.MAX_VALUE,
                itemSpacing = 4,
                cornerSize = 0,
                columns = 3,
            )
        assertEquals(Int.MAX_VALUE, state.contentPadding)
    }

    @Test
    fun shouldPreserveLargeItemSpacing() {
        val state =
            GalleryPickerState(
                contentPadding = 4,
                itemSpacing = Int.MAX_VALUE,
                cornerSize = 0,
                columns = 3,
            )
        assertEquals(Int.MAX_VALUE, state.itemSpacing)
    }

    @Test
    fun shouldPreserveLargeCornerSize() {
        val state =
            GalleryPickerState(
                contentPadding = 4,
                itemSpacing = 4,
                cornerSize = Int.MAX_VALUE,
                columns = 3,
            )
        assertEquals(Int.MAX_VALUE, state.cornerSize)
    }

    @Test
    fun shouldPreserveLargeColumns() {
        val state =
            GalleryPickerState(
                contentPadding = 4,
                itemSpacing = 4,
                cornerSize = 0,
                columns = Int.MAX_VALUE,
            )
        assertEquals(Int.MAX_VALUE, state.columns)
    }

    // --- Negative values (no validation in constructor) ---

    @Test
    fun shouldAcceptNegativeContentPadding() {
        val state =
            GalleryPickerState(
                contentPadding = -1,
                itemSpacing = 4,
                cornerSize = 0,
                columns = 3,
            )
        assertEquals(-1, state.contentPadding)
    }

    @Test
    fun shouldAcceptNegativeItemSpacing() {
        val state =
            GalleryPickerState(
                contentPadding = 4,
                itemSpacing = -1,
                cornerSize = 0,
                columns = 3,
            )
        assertEquals(-1, state.itemSpacing)
    }

    @Test
    fun shouldAcceptNegativeCornerSize() {
        val state =
            GalleryPickerState(
                contentPadding = 4,
                itemSpacing = 4,
                cornerSize = -1,
                columns = 3,
            )
        assertEquals(-1, state.cornerSize)
    }

    @Test
    fun shouldAcceptZeroColumns() {
        val state =
            GalleryPickerState(
                contentPadding = 4,
                itemSpacing = 4,
                cornerSize = 0,
                columns = 0,
            )
        assertEquals(0, state.columns)
    }

    // --- Single column ---

    @Test
    fun shouldSupportSingleColumn() {
        val state =
            GalleryPickerState(
                contentPadding = 0,
                itemSpacing = 0,
                cornerSize = 0,
                columns = 1,
            )
        assertEquals(1, state.columns)
    }

    // --- Distinct instances ---

    @Test
    fun twoStatesWithSameValuesShouldBeDifferentInstances() {
        val a = GalleryPickerState(contentPadding = 4, itemSpacing = 4, cornerSize = 0, columns = 3)
        val b = GalleryPickerState(contentPadding = 4, itemSpacing = 4, cornerSize = 0, columns = 3)
        assertNotSame(a, b)
    }

    // --- All fields different ---

    @Test
    fun shouldPreserveAllDistinctFields() {
        val state =
            GalleryPickerState(
                contentPadding = 10,
                itemSpacing = 20,
                cornerSize = 30,
                columns = 5,
            )
        assertEquals(10, state.contentPadding)
        assertEquals(20, state.itemSpacing)
        assertEquals(30, state.cornerSize)
        assertEquals(5, state.columns)
    }

    // --- Typical gallery configurations ---

    @Test
    fun shouldSupportCompactConfiguration() {
        val state =
            GalleryPickerState(
                contentPadding = 2,
                itemSpacing = 2,
                cornerSize = 4,
                columns = 4,
            )
        assertEquals(2, state.contentPadding)
        assertEquals(2, state.itemSpacing)
        assertEquals(4, state.cornerSize)
        assertEquals(4, state.columns)
    }

    @Test
    fun shouldSupportSpacedConfiguration() {
        val state =
            GalleryPickerState(
                contentPadding = 16,
                itemSpacing = 12,
                cornerSize = 16,
                columns = 2,
            )
        assertEquals(16, state.contentPadding)
        assertEquals(12, state.itemSpacing)
        assertEquals(16, state.cornerSize)
        assertEquals(2, state.columns)
    }
}
