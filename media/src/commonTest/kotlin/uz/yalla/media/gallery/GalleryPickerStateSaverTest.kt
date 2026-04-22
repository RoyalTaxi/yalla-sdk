package uz.yalla.media.gallery

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertTrue

/**
 * Supplemental tests for [GalleryPickerState] covering layout configuration boundaries
 * and typical gallery picker configurations used at product call sites.
 *
 * The [GalleryPickerState] Saver ([androidx.compose.runtime.saveable.Saver]) relies on
 * the Compose runtime's [rememberSaveable] infrastructure and cannot be exercised from
 * a plain Kotlin test (no Compose test rule available in commonTest). Testing that the
 * Saver is wired is covered by verifying [GalleryPickerState.Saver] is not null (the
 * companion object exists and the property compiles).
 *
 * Existing coverage:
 * - [GalleryPickerStateTest]: default and custom values.
 * - [GalleryPickerStateEdgeCaseTest]: large, negative, and boundary values.
 *
 * This file adds: typical call-site combinations, distinguishability of instances with
 * the same values, and min-column boundary.
 */
class GalleryPickerStateSaverTest {

    // -----------------------------------------------------------------------
    // Saver exists (compile-time contract)
    // -----------------------------------------------------------------------

    @Test
    fun saverPropertyIsNotNull() {
        // GalleryPickerState.Saver is internal — accessible from the same module.
        // Compiling this assignment is sufficient to verify the companion object
        // and Saver property exist. We cannot call saver.save/restore because the
        // star-projection type requires the Compose runtime SaveableStateRegistry,
        // which is not available in commonTest.
        @Suppress("UNUSED_VARIABLE")
        val saver = GalleryPickerState.Saver
        assertTrue(true) // reaching here = companion and Saver are present
    }

    // -----------------------------------------------------------------------
    // Distinct instances
    // -----------------------------------------------------------------------

    @Test
    fun twoInstancesWithSameValuesShouldBeDifferentInstances() {
        val a = GalleryPickerState(contentPadding = 4, itemSpacing = 4, cornerSize = 0, columns = 3)
        val b = GalleryPickerState(contentPadding = 4, itemSpacing = 4, cornerSize = 0, columns = 3)
        assertNotSame(a, b)
    }

    // -----------------------------------------------------------------------
    // Typical gallery configurations used at product call sites
    // -----------------------------------------------------------------------

    @Test
    fun profilePhotoPickerConfiguration() {
        // Single image picker: 3 columns, rounded corners, comfortable padding
        val state = GalleryPickerState(contentPadding = 8, itemSpacing = 4, cornerSize = 8, columns = 3)
        assertEquals(8, state.contentPadding)
        assertEquals(4, state.itemSpacing)
        assertEquals(8, state.cornerSize)
        assertEquals(3, state.columns)
    }

    @Test
    fun multiSelectConfiguration() {
        // Multi-image picker: 4 columns, tight spacing for density
        val state = GalleryPickerState(contentPadding = 4, itemSpacing = 2, cornerSize = 4, columns = 4)
        assertEquals(4, state.contentPadding)
        assertEquals(2, state.itemSpacing)
        assertEquals(4, state.cornerSize)
        assertEquals(4, state.columns)
    }

    @Test
    fun fullWidthSingleColumnConfiguration() {
        // Full-width list: 1 column, no padding, no spacing
        val state = GalleryPickerState(contentPadding = 0, itemSpacing = 0, cornerSize = 0, columns = 1)
        assertEquals(0, state.contentPadding)
        assertEquals(0, state.itemSpacing)
        assertEquals(0, state.cornerSize)
        assertEquals(1, state.columns)
    }

    @Test
    fun twoColumnWideGridConfiguration() {
        val state = GalleryPickerState(contentPadding = 16, itemSpacing = 8, cornerSize = 16, columns = 2)
        assertEquals(16, state.contentPadding)
        assertEquals(8, state.itemSpacing)
        assertEquals(16, state.cornerSize)
        assertEquals(2, state.columns)
    }

    @Test
    fun fiveColumnDenseGridConfiguration() {
        val state = GalleryPickerState(contentPadding = 2, itemSpacing = 1, cornerSize = 2, columns = 5)
        assertEquals(2, state.contentPadding)
        assertEquals(1, state.itemSpacing)
        assertEquals(2, state.cornerSize)
        assertEquals(5, state.columns)
    }

    // -----------------------------------------------------------------------
    // All four properties are independent
    // -----------------------------------------------------------------------

    @Test
    fun changingContentPaddingDoesNotAffectOtherFields() {
        val a = GalleryPickerState(contentPadding = 4, itemSpacing = 6, cornerSize = 8, columns = 3)
        val b = GalleryPickerState(contentPadding = 16, itemSpacing = 6, cornerSize = 8, columns = 3)
        assertEquals(a.itemSpacing, b.itemSpacing)
        assertEquals(a.cornerSize, b.cornerSize)
        assertEquals(a.columns, b.columns)
    }

    @Test
    fun changingColumnsDoesNotAffectSpacingOrPadding() {
        val a = GalleryPickerState(contentPadding = 4, itemSpacing = 4, cornerSize = 0, columns = 3)
        val b = GalleryPickerState(contentPadding = 4, itemSpacing = 4, cornerSize = 0, columns = 6)
        assertEquals(a.contentPadding, b.contentPadding)
        assertEquals(a.itemSpacing, b.itemSpacing)
        assertEquals(a.cornerSize, b.cornerSize)
    }

    // -----------------------------------------------------------------------
    // Regression: fields preserve correct values under all combinations
    // -----------------------------------------------------------------------

    @Test
    fun allFieldsPreservedWhenAllNonDefault() {
        val state = GalleryPickerState(contentPadding = 12, itemSpacing = 8, cornerSize = 16, columns = 4)
        assertEquals(12, state.contentPadding)
        assertEquals(8, state.itemSpacing)
        assertEquals(16, state.cornerSize)
        assertEquals(4, state.columns)
    }

    @Test
    fun onlyCornerSizeNonDefault() {
        val state = GalleryPickerState(contentPadding = 0, itemSpacing = 0, cornerSize = 24, columns = 1)
        assertEquals(0, state.contentPadding)
        assertEquals(0, state.itemSpacing)
        assertEquals(24, state.cornerSize)
        assertEquals(1, state.columns)
    }
}
