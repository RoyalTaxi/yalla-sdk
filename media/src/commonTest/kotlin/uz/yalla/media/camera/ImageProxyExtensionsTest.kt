package uz.yalla.media.camera

import kotlin.test.Ignore
import kotlin.test.Test

/**
 * Tests for [ImageProxy.toByteArray] and [Bitmap.rotate] in [ImageProxyExtensions.kt].
 *
 * These functions are `internal` and reside in `androidMain` only (they depend on
 * `androidx.camera.core.ImageProxy` and `android.graphics.Bitmap`). They cannot be
 * exercised from `commonTest` without the Android framework present.
 *
 * TODO (Phase 6): Wire the `androidUnitTest` source set in `media/build.gradle.kts`
 *  and move these tests there. Use Robolectric or MockK + a fake ImageProxy to:
 *  - Assert [ImageProxy.toByteArray] applies 0°, 90°, 180°, 270° rotations correctly.
 *  - Assert width/height are swapped for 90° and 270° rotations.
 *  - Assert [Bitmap.rotate] encodes the rotated bitmap as JPEG (non-empty byte array).
 *  - Assert [Bitmap.toByteArray] (the extension in ImageProxyExtensions) produces
 *    a non-empty JPEG byte array at quality 100.
 */
class ImageProxyExtensionsTest {

    @Ignore
    @Test
    fun imageProxyToByteArrayAppliesZeroRotation() {
        // TODO Phase 6: create a fake ImageProxy with rotationDegrees = 0,
        // call toByteArray(), verify the result is non-empty and the bitmap
        // dimensions are unchanged.
    }

    @Ignore
    @Test
    fun imageProxyToByteArrayApplies90DegreeRotation() {
        // TODO Phase 6: create a fake ImageProxy with rotationDegrees = 90 and
        // a 100×50 source bitmap. After toByteArray(), decode the JPEG and verify
        // the resulting bitmap is 50×100 (width/height swapped).
    }

    @Ignore
    @Test
    fun imageProxyToByteArrayApplies180DegreeRotation() {
        // TODO Phase 6: rotationDegrees = 180 — dimensions should be unchanged
        // (same width/height), but the content should be flipped.
    }

    @Ignore
    @Test
    fun imageProxyToByteArrayApplies270DegreeRotation() {
        // TODO Phase 6: rotationDegrees = 270 — like 90°, width and height swap.
    }

    @Ignore
    @Test
    fun imageProxyIsClosedAfterConversion() {
        // TODO Phase 6: verify that close() is called on the ImageProxy exactly
        // once after toByteArray() completes.
    }
}
