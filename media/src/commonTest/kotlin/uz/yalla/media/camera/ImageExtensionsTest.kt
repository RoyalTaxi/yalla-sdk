package uz.yalla.media.camera

import kotlin.test.Ignore
import kotlin.test.Test

/**
 * Tests for the `Bitmap` extension functions in [ImageProxyExtensions.kt]:
 * [Bitmap.rotate] and [Bitmap.toByteArray].
 *
 * These extensions are `internal` and live in `androidMain`, so they depend on
 * `android.graphics.Bitmap` and `android.graphics.Matrix` — neither of which is
 * available in the Kotlin/Common or Kotlin/Native runtime.
 *
 * TODO (Phase 6): Wire the `androidUnitTest` source set in `media/build.gradle.kts`.
 *  With Robolectric present, test:
 *  - [Bitmap.toByteArray]: produces a non-empty JPEG byte array; re-decoding it gives
 *    a Bitmap with the same width and height as the original.
 *  - [Bitmap.rotate] at 0°: output bitmap dimensions match the input (w×h unchanged).
 *  - [Bitmap.rotate] at 90°: output is h×w (width and height swap).
 *  - [Bitmap.rotate] at 180°: output is w×h (same dimensions as input).
 *  - [Bitmap.rotate] at 270°: output is h×w (width and height swap).
 *  - [Bitmap.rotate] returns a valid JPEG byte array (non-empty, decodable).
 */
class ImageExtensionsTest {

    @Ignore
    @Test
    fun bitmapToByteArrayProducesNonEmptyJpeg() {
        // TODO Phase 6: create a 100×100 ARGB_8888 Bitmap, call toByteArray(),
        // assert the result is non-empty and can be decoded back to a Bitmap.
    }

    @Ignore
    @Test
    fun bitmapToByteArrayPreservesDimensions() {
        // TODO Phase 6: encode a 200×100 Bitmap, decode the JPEG, verify
        // the decoded Bitmap is 200×100.
    }

    @Ignore
    @Test
    fun bitmapRotate0KeepsDimensions() {
        // TODO Phase 6: rotate a 200×100 Bitmap by 0° — decoded result is 200×100.
    }

    @Ignore
    @Test
    fun bitmapRotate90SwapsDimensions() {
        // TODO Phase 6: rotate a 200×100 Bitmap by 90° — decoded result is 100×200.
    }

    @Ignore
    @Test
    fun bitmapRotate180KeepsDimensions() {
        // TODO Phase 6: rotate a 200×100 Bitmap by 180° — decoded result is 200×100.
    }

    @Ignore
    @Test
    fun bitmapRotate270SwapsDimensions() {
        // TODO Phase 6: rotate a 200×100 Bitmap by 270° — decoded result is 100×200.
    }

    @Ignore
    @Test
    fun bitmapRotateReturnsValidJpeg() {
        // TODO Phase 6: call rotate(90) on any Bitmap, verify the byte array
        // is non-empty and starts with the JPEG SOI marker (0xFF 0xD8).
    }
}
