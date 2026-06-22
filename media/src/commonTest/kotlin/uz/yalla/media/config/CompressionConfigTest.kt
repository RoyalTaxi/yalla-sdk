package uz.yalla.media.config

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class CompressionConfigTest {

    @Test
    fun defaultPresetValues() {
        assertEquals(1024 * 1024, CompressionConfig.Default.maxFileSize)
        assertEquals(1024, CompressionConfig.Default.maxDimension)
        assertEquals(80, CompressionConfig.Default.quality)
    }

    @Test
    fun profilePhotoPresetValues() {
        assertEquals(512 * 1024, CompressionConfig.ProfilePhoto.maxFileSize)
        assertEquals(512, CompressionConfig.ProfilePhoto.maxDimension)
        assertEquals(85, CompressionConfig.ProfilePhoto.quality)
    }

    @Test
    fun chatImagePresetValues() {
        assertEquals(2 * 1024 * 1024, CompressionConfig.ChatImage.maxFileSize)
        assertEquals(1920, CompressionConfig.ChatImage.maxDimension)
        assertEquals(75, CompressionConfig.ChatImage.quality)
    }

    @Test
    fun allPresetsAreWithinInvariants() {
        listOf(
            CompressionConfig.Default,
            CompressionConfig.ProfilePhoto,
            CompressionConfig.ChatImage
        ).forEach { config ->
            assertTrue(config.quality in 1..100, "quality ${config.quality} out of 1..100")
            assertTrue(config.maxFileSize > 0, "maxFileSize must be positive")
            assertTrue(config.maxDimension > 0, "maxDimension must be positive")
        }
    }

    @Test
    fun rejectsQualityBelowFloor() {
        assertFailsWith<IllegalArgumentException> {
            CompressionConfig(maxFileSize = 1024, maxDimension = 256, quality = 5)
        }
    }

    @Test
    fun rejectsQualityAboveCeiling() {
        assertFailsWith<IllegalArgumentException> {
            CompressionConfig(maxFileSize = 1024, maxDimension = 256, quality = 101)
        }
    }

    @Test
    fun rejectsZeroQuality() {
        assertFailsWith<IllegalArgumentException> {
            CompressionConfig(maxFileSize = 1024, maxDimension = 256, quality = 0)
        }
    }

    @Test
    fun rejectsNonPositiveMaxDimension() {
        assertFailsWith<IllegalArgumentException> {
            CompressionConfig(maxFileSize = 1024, maxDimension = 0, quality = 80)
        }
    }

    @Test
    fun rejectsNonPositiveMaxFileSize() {
        assertFailsWith<IllegalArgumentException> {
            CompressionConfig(maxFileSize = 0, maxDimension = 256, quality = 80)
        }
    }

    @Test
    fun preservesCustomValues() {
        val custom = CompressionConfig(maxFileSize = 256 * 1024, maxDimension = 256, quality = 90)
        assertEquals(256 * 1024, custom.maxFileSize)
        assertEquals(256, custom.maxDimension)
        assertEquals(90, custom.quality)
    }
}
