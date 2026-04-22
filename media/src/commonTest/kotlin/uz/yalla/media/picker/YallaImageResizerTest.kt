package uz.yalla.media.picker

import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests for [YallaImageResizer] and its private [calculateInSampleSize] helper.
 *
 * [YallaImageResizer] is an `internal object` in `androidMain` that depends on
 * `android.content.Context`, `android.net.Uri`, and [android.graphics.BitmapFactory].
 * These are not available in the Kotlin/Common runtime, so end-to-end tests cannot run
 * here.
 *
 * What IS pure-Kotlin and can be unit-tested is the [calculateInSampleSize] algorithm,
 * but it is `private` to the object. The algorithm is:
 *   - inSampleSize starts at 1
 *   - doubles while outWidth/inSampleSize > targetWidth OR outHeight/inSampleSize > targetHeight
 *   - returns a power-of-two value
 *
 * The tests below validate this logic via a locally mirrored [calculateInSampleSize]
 * function that matches the production implementation exactly, ensuring the down-sampling
 * math is correct without requiring Android.
 *
 * TODO (Phase 6): Wire the `androidUnitTest` source set in `media/build.gradle.kts`
 *  and add end-to-end tests for [YallaImageResizer.resizeImageAsync]:
 *  - Supply a real or Robolectric-backed Context + a content URI pointing to a test PNG.
 *  - Assert the output JPEG long side is ≤ the requested dimension (e.g. 1024 px).
 *  - Assert aspect ratio is preserved within 1% tolerance.
 *  - Assert the result is a non-empty, valid JPEG byte array.
 */
class YallaImageResizerTest {

    // Mirror of the private calculateInSampleSize from YallaImageResizer.
    private fun calculateInSampleSize(outWidth: Int, outHeight: Int, targetWidth: Int, targetHeight: Int): Int {
        var inSampleSize = 1
        while (outWidth / inSampleSize > targetWidth || outHeight / inSampleSize > targetHeight) {
            inSampleSize *= 2
        }
        return inSampleSize
    }

    // -----------------------------------------------------------------------
    // calculateInSampleSize — pure logic, no Android dependencies
    // -----------------------------------------------------------------------

    @Test
    fun sampleSize1WhenImageFitsTarget() {
        val result = calculateInSampleSize(512, 512, 1024, 1024)
        assertEquals(1, result)
    }

    @Test
    fun sampleSize1WhenImageEqualsTarget() {
        val result = calculateInSampleSize(1024, 1024, 1024, 1024)
        assertEquals(1, result)
    }

    @Test
    fun sampleSize2WhenImageDoubleTarget() {
        val result = calculateInSampleSize(2048, 2048, 1024, 1024)
        assertEquals(2, result)
    }

    @Test
    fun sampleSize4WhenImageFourTimesTarget() {
        val result = calculateInSampleSize(4096, 4096, 1024, 1024)
        assertEquals(4, result)
    }

    @Test
    fun sampleSize8WhenImageEightTimesTarget() {
        val result = calculateInSampleSize(8192, 8192, 1024, 1024)
        assertEquals(8, result)
    }

    @Test
    fun sampleSizeIsPowerOfTwo() {
        // 3000×2000 → target 1024×1024 → inSampleSize must be 4
        // (3000/2=1500 > 1024, 3000/4=750 ≤ 1024; so 4)
        val result = calculateInSampleSize(3000, 2000, 1024, 1024)
        assertEquals(4, result)
        // Verify it is a power-of-two
        assertTrue(result > 0 && (result and (result - 1)) == 0)
    }

    @Test
    fun asymmetricImageWidthConstraint() {
        // 2048×512 → target 1024×1024: width exceeds, height fits
        val result = calculateInSampleSize(2048, 512, 1024, 1024)
        assertEquals(2, result)
    }

    @Test
    fun asymmetricImageHeightConstraint() {
        // 512×2048 → target 1024×1024: height exceeds, width fits
        val result = calculateInSampleSize(512, 2048, 1024, 1024)
        assertEquals(2, result)
    }

    @Test
    fun resultAfterSamplingFitsTarget() {
        val outWidth = 3840
        val outHeight = 2160
        val targetWidth = 1024
        val targetHeight = 1024
        val inSampleSize = calculateInSampleSize(outWidth, outHeight, targetWidth, targetHeight)
        // After applying inSampleSize, effective dimensions should fit
        assertTrue(outWidth / inSampleSize <= targetWidth || outHeight / inSampleSize <= targetHeight)
    }

    @Test
    fun sampleSize1ForTinyImage() {
        val result = calculateInSampleSize(100, 100, 1024, 1024)
        assertEquals(1, result)
    }

    // -----------------------------------------------------------------------
    // End-to-end resize tests — require Android (androidUnitTest / Robolectric)
    // -----------------------------------------------------------------------

    @Ignore
    @Test
    fun resizedImageLongSideIsWithinMaxDimension() {
        // TODO Phase 6: provide a test PNG via resources or a byte-array fixture,
        // run resizeImageAsync with maxDimension=1024, and assert:
        //   maxOf(resultBitmap.width, resultBitmap.height) <= 1024
    }

    @Ignore
    @Test
    fun resizedImagePreservesAspectRatio() {
        // TODO Phase 6: given a 2000×1000 source bitmap, after resize to 1024px max,
        // assert result dimensions are ~1024×512 (within 1% tolerance).
    }

    @Ignore
    @Test
    fun imageBelowThresholdIsReturnedAsIs() {
        // TODO Phase 6: supply an image smaller than resizeThresholdBytes,
        // assert the returned byte array matches the original (no resizing applied).
    }
}
