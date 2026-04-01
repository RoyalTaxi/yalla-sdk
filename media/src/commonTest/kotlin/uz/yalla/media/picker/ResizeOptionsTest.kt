package uz.yalla.media.picker

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class ResizeOptionsTest {
    // --- Defaults ---

    @Test
    fun defaultWidthShouldBe800() {
        assertEquals(800, ResizeOptions().width)
    }

    @Test
    fun defaultHeightShouldBe800() {
        assertEquals(800, ResizeOptions().height)
    }

    @Test
    fun defaultResizeThresholdShouldBe1Mb() {
        assertEquals(1_048_576L, ResizeOptions().resizeThresholdBytes)
    }

    @Test
    fun defaultCompressionQualityShouldBe1() {
        assertEquals(1.0, ResizeOptions().compressionQuality)
    }

    // --- Custom values ---

    @Test
    fun shouldPreserveCustomWidth() {
        val options = ResizeOptions(width = 1920)
        assertEquals(1920, options.width)
    }

    @Test
    fun shouldPreserveCustomHeight() {
        val options = ResizeOptions(height = 1080)
        assertEquals(1080, options.height)
    }

    @Test
    fun shouldPreserveCustomResizeThreshold() {
        val options = ResizeOptions(resizeThresholdBytes = 2_097_152L)
        assertEquals(2_097_152L, options.resizeThresholdBytes)
    }

    @Test
    fun shouldPreserveCustomCompressionQuality() {
        val options = ResizeOptions(compressionQuality = 0.5)
        assertEquals(0.5, options.compressionQuality)
    }

    // --- Edge cases ---

    @Test
    fun shouldAcceptZeroWidth() {
        val options = ResizeOptions(width = 0)
        assertEquals(0, options.width)
    }

    @Test
    fun shouldAcceptZeroHeight() {
        val options = ResizeOptions(height = 0)
        assertEquals(0, options.height)
    }

    @Test
    fun shouldAcceptZeroResizeThreshold() {
        val options = ResizeOptions(resizeThresholdBytes = 0L)
        assertEquals(0L, options.resizeThresholdBytes)
    }

    @Test
    fun shouldAcceptMinCompressionQuality() {
        val options = ResizeOptions(compressionQuality = 0.0)
        assertEquals(0.0, options.compressionQuality)
    }

    @Test
    fun shouldAcceptMaxCompressionQuality() {
        val options = ResizeOptions(compressionQuality = 1.0)
        assertEquals(1.0, options.compressionQuality)
    }

    @Test
    fun shouldAcceptLargeResizeThreshold() {
        val options = ResizeOptions(resizeThresholdBytes = Long.MAX_VALUE)
        assertEquals(Long.MAX_VALUE, options.resizeThresholdBytes)
    }

    @Test
    fun shouldAcceptLargeDimensions() {
        val options = ResizeOptions(width = Int.MAX_VALUE, height = Int.MAX_VALUE)
        assertEquals(Int.MAX_VALUE, options.width)
        assertEquals(Int.MAX_VALUE, options.height)
    }

    // --- Equality ---

    @Test
    fun sameDefaultsShouldBeEqual() {
        assertEquals(ResizeOptions(), ResizeOptions())
    }

    @Test
    fun sameCustomValuesShouldBeEqual() {
        val a = ResizeOptions(width = 640, height = 480, resizeThresholdBytes = 500L, compressionQuality = 0.8)
        val b = ResizeOptions(width = 640, height = 480, resizeThresholdBytes = 500L, compressionQuality = 0.8)
        assertEquals(a, b)
    }

    @Test
    fun differentWidthShouldNotBeEqual() {
        assertNotEquals(ResizeOptions(width = 100), ResizeOptions(width = 200))
    }

    @Test
    fun differentHeightShouldNotBeEqual() {
        assertNotEquals(ResizeOptions(height = 100), ResizeOptions(height = 200))
    }

    @Test
    fun differentResizeThresholdShouldNotBeEqual() {
        assertNotEquals(
            ResizeOptions(resizeThresholdBytes = 100L),
            ResizeOptions(resizeThresholdBytes = 200L),
        )
    }

    @Test
    fun differentCompressionQualityShouldNotBeEqual() {
        assertNotEquals(
            ResizeOptions(compressionQuality = 0.5),
            ResizeOptions(compressionQuality = 0.9),
        )
    }

    // --- HashCode ---

    @Test
    fun equalOptionsShouldHaveSameHashCode() {
        val a = ResizeOptions(width = 1024, height = 768)
        val b = ResizeOptions(width = 1024, height = 768)
        assertEquals(a.hashCode(), b.hashCode())
    }

    // --- Copy ---

    @Test
    fun copyShouldPreserveUnchangedFields() {
        val original = ResizeOptions(width = 640, height = 480, resizeThresholdBytes = 1000L, compressionQuality = 0.7)
        val copy = original.copy(width = 1024)

        assertEquals(1024, copy.width)
        assertEquals(480, copy.height)
        assertEquals(1000L, copy.resizeThresholdBytes)
        assertEquals(0.7, copy.compressionQuality)
    }

    @Test
    fun copyShouldNotMutateOriginal() {
        val original = ResizeOptions(width = 640)
        val copy = original.copy(width = 1024)

        assertEquals(640, original.width)
        assertEquals(1024, copy.width)
    }

    @Test
    fun copyWithNoArgsShouldEqualOriginal() {
        val original = ResizeOptions(width = 640, height = 480)
        val copy = original.copy()
        assertEquals(original, copy)
    }

    // --- toString ---

    @Test
    fun toStringShouldContainAllFieldNames() {
        val str = ResizeOptions().toString()
        assertTrue(str.contains("width"))
        assertTrue(str.contains("height"))
        assertTrue(str.contains("resizeThresholdBytes"))
        assertTrue(str.contains("compressionQuality"))
    }
}
