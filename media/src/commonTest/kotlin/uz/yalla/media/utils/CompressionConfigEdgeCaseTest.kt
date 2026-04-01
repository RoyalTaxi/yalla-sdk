package uz.yalla.media.utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class CompressionConfigEdgeCaseTest {
    // --- Preset distinctness ---

    @Test
    fun defaultShouldNotEqualProfilePhoto() {
        assertNotEquals(CompressionConfig.Default, CompressionConfig.ProfilePhoto)
    }

    @Test
    fun defaultShouldNotEqualChatImage() {
        assertNotEquals(CompressionConfig.Default, CompressionConfig.ChatImage)
    }

    @Test
    fun profilePhotoShouldNotEqualChatImage() {
        assertNotEquals(CompressionConfig.ProfilePhoto, CompressionConfig.ChatImage)
    }

    // --- Preset ordering invariants ---

    @Test
    fun profilePhotoShouldHaveSmallerMaxFileSizeThanDefault() {
        assertTrue(CompressionConfig.ProfilePhoto.maxFileSize < CompressionConfig.Default.maxFileSize)
    }

    @Test
    fun chatImageShouldHaveLargerMaxFileSizeThanDefault() {
        assertTrue(CompressionConfig.ChatImage.maxFileSize > CompressionConfig.Default.maxFileSize)
    }

    @Test
    fun profilePhotoShouldHaveSmallerMaxDimensionThanDefault() {
        assertTrue(CompressionConfig.ProfilePhoto.maxDimension < CompressionConfig.Default.maxDimension)
    }

    @Test
    fun chatImageShouldHaveLargerMaxDimensionThanDefault() {
        assertTrue(CompressionConfig.ChatImage.maxDimension > CompressionConfig.Default.maxDimension)
    }

    // --- Custom config equality ---

    @Test
    fun sameCustomValuesShouldBeEqual() {
        val a = CompressionConfig(maxFileSize = 1000, maxDimension = 500, quality = 70)
        val b = CompressionConfig(maxFileSize = 1000, maxDimension = 500, quality = 70)
        assertEquals(a, b)
    }

    @Test
    fun differentMaxFileSizeShouldNotBeEqual() {
        val a = CompressionConfig(maxFileSize = 1000, maxDimension = 500, quality = 70)
        val b = CompressionConfig(maxFileSize = 2000, maxDimension = 500, quality = 70)
        assertNotEquals(a, b)
    }

    @Test
    fun differentMaxDimensionShouldNotBeEqual() {
        val a = CompressionConfig(maxFileSize = 1000, maxDimension = 500, quality = 70)
        val b = CompressionConfig(maxFileSize = 1000, maxDimension = 600, quality = 70)
        assertNotEquals(a, b)
    }

    @Test
    fun differentQualityShouldNotBeEqual() {
        val a = CompressionConfig(maxFileSize = 1000, maxDimension = 500, quality = 70)
        val b = CompressionConfig(maxFileSize = 1000, maxDimension = 500, quality = 80)
        assertNotEquals(a, b)
    }

    // --- HashCode consistency ---

    @Test
    fun equalConfigsShouldHaveSameHashCode() {
        val a = CompressionConfig(maxFileSize = 1000, maxDimension = 500, quality = 70)
        val b = CompressionConfig(maxFileSize = 1000, maxDimension = 500, quality = 70)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun presetsShouldHaveDistinctHashCodes() {
        val hashCodes =
            setOf(
                CompressionConfig.Default.hashCode(),
                CompressionConfig.ProfilePhoto.hashCode(),
                CompressionConfig.ChatImage.hashCode(),
            )
        assertEquals(3, hashCodes.size)
    }

    // --- Copy behavior ---

    @Test
    fun copyShouldPreserveUnchangedFields() {
        val original = CompressionConfig(maxFileSize = 1000, maxDimension = 500, quality = 70)
        val copy = original.copy(quality = 90)

        assertEquals(1000, copy.maxFileSize)
        assertEquals(500, copy.maxDimension)
        assertEquals(90, copy.quality)
    }

    @Test
    fun copyShouldNotMutateOriginal() {
        val original = CompressionConfig(maxFileSize = 1000, maxDimension = 500, quality = 70)
        val copy = original.copy(quality = 90)

        assertEquals(70, original.quality)
        assertEquals(90, copy.quality)
    }

    @Test
    fun copyWithNoArgsShouldEqualOriginal() {
        val original = CompressionConfig.Default
        val copy = original.copy()
        assertEquals(original, copy)
    }

    // --- Boundary quality values ---

    @Test
    fun shouldAcceptQuality1() {
        val config = CompressionConfig(maxFileSize = 1000, maxDimension = 100, quality = 1)
        assertEquals(1, config.quality)
    }

    @Test
    fun shouldAcceptQuality100() {
        val config = CompressionConfig(maxFileSize = 1000, maxDimension = 100, quality = 100)
        assertEquals(100, config.quality)
    }

    // --- Extreme dimension values ---

    @Test
    fun shouldAcceptMaxDimension1() {
        val config = CompressionConfig(maxFileSize = 1000, maxDimension = 1, quality = 80)
        assertEquals(1, config.maxDimension)
    }

    @Test
    fun shouldAcceptLargeMaxDimension() {
        val config = CompressionConfig(maxFileSize = 1000, maxDimension = 10000, quality = 80)
        assertEquals(10000, config.maxDimension)
    }

    // --- Extreme file size values ---

    @Test
    fun shouldAcceptMinimalMaxFileSize() {
        val config = CompressionConfig(maxFileSize = 1, maxDimension = 100, quality = 80)
        assertEquals(1, config.maxFileSize)
    }

    @Test
    fun shouldAcceptLargeMaxFileSize() {
        val config = CompressionConfig(maxFileSize = Int.MAX_VALUE, maxDimension = 100, quality = 80)
        assertEquals(Int.MAX_VALUE, config.maxFileSize)
    }

    // --- toString ---

    @Test
    fun toStringShouldContainAllFields() {
        val str = CompressionConfig.Default.toString()
        assertTrue(str.contains("maxFileSize"))
        assertTrue(str.contains("maxDimension"))
        assertTrue(str.contains("quality"))
    }

    // --- Custom config matching preset values ---

    @Test
    fun customConfigMatchingDefaultPresetShouldEqual() {
        val custom =
            CompressionConfig(
                maxFileSize = 1024 * 1024,
                maxDimension = 1024,
                quality = 80,
            )
        assertEquals(CompressionConfig.Default, custom)
    }

    @Test
    fun customConfigMatchingProfilePhotoPresetShouldEqual() {
        val custom =
            CompressionConfig(
                maxFileSize = 512 * 1024,
                maxDimension = 512,
                quality = 85,
            )
        assertEquals(CompressionConfig.ProfilePhoto, custom)
    }

    @Test
    fun customConfigMatchingChatImagePresetShouldEqual() {
        val custom =
            CompressionConfig(
                maxFileSize = 2 * 1024 * 1024,
                maxDimension = 1920,
                quality = 75,
            )
        assertEquals(CompressionConfig.ChatImage, custom)
    }
}
