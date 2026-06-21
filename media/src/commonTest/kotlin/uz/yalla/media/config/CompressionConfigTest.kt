package uz.yalla.media.config

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Characterization of the public [CompressionConfig] contract: the three preset values a buyer's app
 * depends on (a silent change to e.g. `ProfilePhoto.maxDimension` is a behavior change) and the
 * `init` validation added so an illegal config fails fast at the API boundary instead of silently
 * disabling the quality search deep in the compressor (review media.md #12).
 */
class CompressionConfigTest {
    // --- Default preset ---

    @Test
    fun defaultPresetValues() {
        assertEquals(1024 * 1024, CompressionConfig.Default.maxFileSize)
        assertEquals(1024, CompressionConfig.Default.maxDimension)
        assertEquals(80, CompressionConfig.Default.quality)
    }

    // --- ProfilePhoto preset ---

    @Test
    fun profilePhotoPresetValues() {
        assertEquals(512 * 1024, CompressionConfig.ProfilePhoto.maxFileSize)
        assertEquals(512, CompressionConfig.ProfilePhoto.maxDimension)
        assertEquals(85, CompressionConfig.ProfilePhoto.quality)
    }

    // --- ChatImage preset ---

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

    // --- Validation: quality must be in 1..100 ---

    @Test
    fun rejectsQualityBelowFloor() {
        // Before the fix, quality < 10 made the binary search a no-op (lo > hi) and silently forced
        // the fallback; now it is rejected at construction.
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

    // --- Validation: positive dimensions / sizes ---

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

    // --- Custom config preserves values ---

    @Test
    fun preservesCustomValues() {
        val custom = CompressionConfig(maxFileSize = 256 * 1024, maxDimension = 256, quality = 90)
        assertEquals(256 * 1024, custom.maxFileSize)
        assertEquals(256, custom.maxDimension)
        assertEquals(90, custom.quality)
    }
}
