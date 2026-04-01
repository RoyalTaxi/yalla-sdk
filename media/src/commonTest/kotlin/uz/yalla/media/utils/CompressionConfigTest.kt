package uz.yalla.media.utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CompressionConfigTest {
    // --- Default preset ---

    @Test
    fun shouldHaveOneMbMaxFileSizeOnDefault() {
        assertEquals(1024 * 1024, CompressionConfig.Default.maxFileSize)
    }

    @Test
    fun shouldHave1024MaxDimensionOnDefault() {
        assertEquals(1024, CompressionConfig.Default.maxDimension)
    }

    @Test
    fun shouldHaveQuality80OnDefault() {
        assertEquals(80, CompressionConfig.Default.quality)
    }

    // --- ProfilePhoto preset ---

    @Test
    fun shouldHave512KbMaxFileSizeOnProfilePhoto() {
        assertEquals(512 * 1024, CompressionConfig.ProfilePhoto.maxFileSize)
    }

    @Test
    fun shouldHave512MaxDimensionOnProfilePhoto() {
        assertEquals(512, CompressionConfig.ProfilePhoto.maxDimension)
    }

    @Test
    fun shouldHaveQuality85OnProfilePhoto() {
        assertEquals(85, CompressionConfig.ProfilePhoto.quality)
    }

    // --- ChatImage preset ---

    @Test
    fun shouldHaveTwoMbMaxFileSizeOnChatImage() {
        assertEquals(2 * 1024 * 1024, CompressionConfig.ChatImage.maxFileSize)
    }

    @Test
    fun shouldHave1920MaxDimensionOnChatImage() {
        assertEquals(1920, CompressionConfig.ChatImage.maxDimension)
    }

    @Test
    fun shouldHaveQuality75OnChatImage() {
        assertEquals(75, CompressionConfig.ChatImage.quality)
    }

    // --- Validation: quality ranges ---

    @Test
    fun shouldHaveQualityWithinValidRangeOnAllPresets() {
        val presets =
            listOf(
                CompressionConfig.Default,
                CompressionConfig.ProfilePhoto,
                CompressionConfig.ChatImage,
            )
        presets.forEach { config ->
            assertTrue(config.quality in 1..100, "quality ${config.quality} out of 1..100")
        }
    }

    // --- Validation: positive sizes ---

    @Test
    fun shouldHavePositiveMaxFileSizeOnAllPresets() {
        val presets =
            listOf(
                CompressionConfig.Default,
                CompressionConfig.ProfilePhoto,
                CompressionConfig.ChatImage,
            )
        presets.forEach { config ->
            assertTrue(config.maxFileSize > 0, "maxFileSize should be positive")
        }
    }

    @Test
    fun shouldHavePositiveMaxDimensionOnAllPresets() {
        val presets =
            listOf(
                CompressionConfig.Default,
                CompressionConfig.ProfilePhoto,
                CompressionConfig.ChatImage,
            )
        presets.forEach { config ->
            assertTrue(config.maxDimension > 0, "maxDimension should be positive")
        }
    }

    // --- Custom config ---

    @Test
    fun shouldPreserveCustomValues() {
        val custom =
            CompressionConfig(
                maxFileSize = 256 * 1024,
                maxDimension = 256,
                quality = 90,
            )
        assertEquals(256 * 1024, custom.maxFileSize)
        assertEquals(256, custom.maxDimension)
        assertEquals(90, custom.quality)
    }
}
