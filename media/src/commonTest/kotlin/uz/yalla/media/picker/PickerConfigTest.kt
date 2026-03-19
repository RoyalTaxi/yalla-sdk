package uz.yalla.media.picker

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals

class PickerConfigTest {

    // --- SelectionMode ---

    @Test
    fun shouldBeSingleInstance() {
        assertIs<SelectionMode.Single>(SelectionMode.Single)
    }

    @Test
    fun shouldHaveInfinityAsDefaultMaxSelection() {
        val mode = SelectionMode.Multiple()
        assertEquals(SelectionMode.INFINITY, mode.maxSelection)
    }

    @Test
    fun shouldPreserveCustomMaxSelection() {
        val mode = SelectionMode.Multiple(maxSelection = 5)
        assertEquals(5, mode.maxSelection)
    }

    @Test
    fun shouldHaveInfinityEqualToZero() {
        assertEquals(0, SelectionMode.INFINITY)
    }

    @Test
    fun shouldDistinguishSingleFromMultiple() {
        val single: SelectionMode = SelectionMode.Single
        val multiple: SelectionMode = SelectionMode.Multiple()
        assertNotEquals(single::class, multiple::class)
    }

    // --- ResizeOptions ---

    @Test
    fun shouldHaveDefaultWidth800() {
        val options = ResizeOptions()
        assertEquals(800, options.width)
    }

    @Test
    fun shouldHaveDefaultHeight800() {
        val options = ResizeOptions()
        assertEquals(800, options.height)
    }

    @Test
    fun shouldHaveDefaultResizeThreshold1Mb() {
        val options = ResizeOptions()
        assertEquals(1_048_576L, options.resizeThresholdBytes)
    }

    @Test
    fun shouldHaveDefaultCompressionQuality1() {
        val options = ResizeOptions()
        assertEquals(1.0, options.compressionQuality)
    }

    @Test
    fun shouldPreserveCustomResizeOptions() {
        val options = ResizeOptions(
            width = 1024,
            height = 768,
            resizeThresholdBytes = 512_000L,
            compressionQuality = 0.75,
        )
        assertEquals(1024, options.width)
        assertEquals(768, options.height)
        assertEquals(512_000L, options.resizeThresholdBytes)
        assertEquals(0.75, options.compressionQuality)
    }

    @Test
    fun shouldSupportCopyWithModificationOnResizeOptions() {
        val original = ResizeOptions(width = 640, height = 480)
        val copy = original.copy(width = 1024)

        assertEquals(640, original.width)
        assertEquals(1024, copy.width)
        assertEquals(480, copy.height)
    }

    // --- FilterOptions ---

    @Test
    fun shouldHaveFourFilterOptions() {
        val filters: List<FilterOptions> = listOf(
            FilterOptions.Default,
            FilterOptions.GrayScale,
            FilterOptions.Sepia,
            FilterOptions.Invert,
        )
        assertEquals(4, filters.size)
    }

    @Test
    fun shouldDistinguishAllFilterOptions() {
        val filters = setOf(
            FilterOptions.Default,
            FilterOptions.GrayScale,
            FilterOptions.Sepia,
            FilterOptions.Invert,
        )
        assertEquals(4, filters.size)
    }

    @Test
    fun shouldBeFilterOptionsInstances() {
        assertIs<FilterOptions>(FilterOptions.Default)
        assertIs<FilterOptions>(FilterOptions.GrayScale)
        assertIs<FilterOptions>(FilterOptions.Sepia)
        assertIs<FilterOptions>(FilterOptions.Invert)
    }
}
