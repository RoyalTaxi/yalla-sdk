package uz.yalla.media.picker

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class FilterOptionsTest {
    // --- Instance type checks ---

    @Test
    fun defaultShouldBeFilterOptions() {
        assertIs<FilterOptions>(FilterOptions.Default)
    }

    @Test
    fun grayScaleShouldBeFilterOptions() {
        assertIs<FilterOptions>(FilterOptions.GrayScale)
    }

    @Test
    fun sepiaShouldBeFilterOptions() {
        assertIs<FilterOptions>(FilterOptions.Sepia)
    }

    @Test
    fun invertShouldBeFilterOptions() {
        assertIs<FilterOptions>(FilterOptions.Invert)
    }

    // --- Singleton identity ---

    @Test
    fun defaultShouldBeSameInstance() {
        assertSame(FilterOptions.Default, FilterOptions.Default)
    }

    @Test
    fun grayScaleShouldBeSameInstance() {
        assertSame(FilterOptions.GrayScale, FilterOptions.GrayScale)
    }

    @Test
    fun sepiaShouldBeSameInstance() {
        assertSame(FilterOptions.Sepia, FilterOptions.Sepia)
    }

    @Test
    fun invertShouldBeSameInstance() {
        assertSame(FilterOptions.Invert, FilterOptions.Invert)
    }

    // --- Distinctness ---

    @Test
    fun allFiltersShouldBeDistinct() {
        val filters =
            setOf(
                FilterOptions.Default,
                FilterOptions.GrayScale,
                FilterOptions.Sepia,
                FilterOptions.Invert,
            )
        assertEquals(4, filters.size)
    }

    @Test
    fun defaultShouldNotEqualGrayScale() {
        assertNotEquals(FilterOptions.Default as FilterOptions, FilterOptions.GrayScale as FilterOptions)
    }

    @Test
    fun defaultShouldNotEqualSepia() {
        assertNotEquals(FilterOptions.Default as FilterOptions, FilterOptions.Sepia as FilterOptions)
    }

    @Test
    fun defaultShouldNotEqualInvert() {
        assertNotEquals(FilterOptions.Default as FilterOptions, FilterOptions.Invert as FilterOptions)
    }

    @Test
    fun grayScaleShouldNotEqualSepia() {
        assertNotEquals(FilterOptions.GrayScale as FilterOptions, FilterOptions.Sepia as FilterOptions)
    }

    @Test
    fun grayScaleShouldNotEqualInvert() {
        assertNotEquals(FilterOptions.GrayScale as FilterOptions, FilterOptions.Invert as FilterOptions)
    }

    @Test
    fun sepiaShouldNotEqualInvert() {
        assertNotEquals(FilterOptions.Sepia as FilterOptions, FilterOptions.Invert as FilterOptions)
    }

    // --- Equality with itself ---

    @Test
    fun defaultShouldEqualItself() {
        assertEquals(FilterOptions.Default, FilterOptions.Default)
    }

    @Test
    fun grayScaleShouldEqualItself() {
        assertEquals(FilterOptions.GrayScale, FilterOptions.GrayScale)
    }

    @Test
    fun sepiaShouldEqualItself() {
        assertEquals(FilterOptions.Sepia, FilterOptions.Sepia)
    }

    @Test
    fun invertShouldEqualItself() {
        assertEquals(FilterOptions.Invert, FilterOptions.Invert)
    }

    // --- Collection usage ---

    @Test
    fun shouldBeUsableAsListElements() {
        val filters =
            listOf(
                FilterOptions.Default,
                FilterOptions.GrayScale,
                FilterOptions.Sepia,
                FilterOptions.Invert,
            )
        assertEquals(4, filters.size)
        assertIs<FilterOptions.Default>(filters[0])
        assertIs<FilterOptions.GrayScale>(filters[1])
        assertIs<FilterOptions.Sepia>(filters[2])
        assertIs<FilterOptions.Invert>(filters[3])
    }

    @Test
    fun shouldBeUsableAsSetElements() {
        val filters = mutableSetOf<FilterOptions>()
        filters.add(FilterOptions.Default)
        filters.add(FilterOptions.GrayScale)
        filters.add(FilterOptions.Sepia)
        filters.add(FilterOptions.Invert)
        // Adding duplicates should not increase size
        filters.add(FilterOptions.Default)
        filters.add(FilterOptions.Invert)
        assertEquals(4, filters.size)
    }

    // --- Pattern matching ---

    @Test
    fun shouldBeExhaustiveInWhenExpression() {
        val filters =
            listOf(
                FilterOptions.Default,
                FilterOptions.GrayScale,
                FilterOptions.Sepia,
                FilterOptions.Invert,
            )
        val names =
            filters.map { filter ->
                when (filter) {
                    FilterOptions.Default -> "default"
                    FilterOptions.GrayScale -> "grayscale"
                    FilterOptions.Sepia -> "sepia"
                    FilterOptions.Invert -> "invert"
                }
            }
        assertEquals(listOf("default", "grayscale", "sepia", "invert"), names)
    }

    // --- Map key usage ---

    @Test
    fun shouldWorkAsMapKey() {
        val map =
            mapOf(
                FilterOptions.Default to "none",
                FilterOptions.GrayScale to "gray",
                FilterOptions.Sepia to "warm",
                FilterOptions.Invert to "negative",
            )
        assertEquals("none", map[FilterOptions.Default])
        assertEquals("gray", map[FilterOptions.GrayScale])
        assertEquals("warm", map[FilterOptions.Sepia])
        assertEquals("negative", map[FilterOptions.Invert])
    }

    // --- Contains check ---

    @Test
    fun listShouldContainAllFilterTypes() {
        val all =
            listOf(
                FilterOptions.Default,
                FilterOptions.GrayScale,
                FilterOptions.Sepia,
                FilterOptions.Invert,
            )
        assertTrue(FilterOptions.Default in all)
        assertTrue(FilterOptions.GrayScale in all)
        assertTrue(FilterOptions.Sepia in all)
        assertTrue(FilterOptions.Invert in all)
    }
}
