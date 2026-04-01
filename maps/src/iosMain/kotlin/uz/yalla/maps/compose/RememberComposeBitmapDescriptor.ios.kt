package uz.yalla.maps.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.window.ComposeUIViewController
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import org.jetbrains.skia.EncodedImageFormat
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSData
import platform.Foundation.create
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetCurrentContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIImage
import platform.UIKit.UIScreen
import kotlin.coroutines.coroutineContext

/**
 * Delay before triggering GraphicsLayer capture, giving the composition enough frames
 * to measure and lay out the content. Too short and the layer may be empty; too long
 * and marker appearance feels sluggish. 50 ms covers two+ frames at 60 fps.
 */
private const val CAPTURE_TRIGGER_DELAY_MS = 50L

/**
 * Maximum time to wait for the composable to report a non-zero size. If this fires,
 * the content likely has zero intrinsic size (e.g. an unbounded lazy layout) and can
 * never be captured.
 */
private const val MEASURE_TIMEOUT_MS = 5_000L

/**
 * Maximum time to wait for GraphicsLayer.toImageBitmap() to complete after capture is
 * triggered. Mirrors [MEASURE_TIMEOUT_MS] — a separate constant so each phase can be
 * tuned independently.
 */
private const val CAPTURE_TIMEOUT_MS = 5_000L

/**
 * Delay after initial layout to allow Compose to settle before measuring. Longer than
 * [CAPTURE_TRIGGER_DELAY_MS] because the off-screen ComposeUIViewController needs
 * extra time for its first layout pass.
 */
private const val LAYOUT_SETTLE_DELAY_MS = 100L

/**
 * iOS implementation of [rememberComposeBitmapDescriptor] using GraphicsLayer capture.
 *
 * This implementation:
 * 1. Creates a temporary ComposeUIViewController to render in a separate composition
 * 2. Uses Compose's rememberGraphicsLayer() API to capture drawing operations
 * 3. Converts the captured ImageBitmap to UIImage via direct pixel buffer transfer
 * 4. Returns a transparent placeholder initially, then the actual image once captured
 *
 * Note: A separate ComposeUIViewController is needed because this composable runs inside
 * GoogleMap's MapApplier context, which doesn't support regular UI composables.
 */
@OptIn(ExperimentalForeignApi::class)
@Composable
@GoogleMapComposable
actual fun rememberComposeBitmapDescriptor(
    vararg keys: Any,
    content: @Composable () -> Unit,
): BitmapDescriptor {
    var capturedImage by remember { mutableStateOf<UIImage?>(null) }

    LaunchedEffect(*keys) {
        try {
            val image =
                withContext(Dispatchers.Main) {
                    captureComposableToUIImage(content)
                }
            // Check if still active before updating state to avoid race conditions
            // during rapid marker addition/removal
            coroutineContext.ensureActive()
            capturedImage = image
        } catch (e: CancellationException) {
            // Don't log cancellation - it's expected during rapid marker updates
            // Re-throw to properly propagate cancellation
            throw e
        } catch (e: Exception) {
            // Non-fatal: capture failed, marker will use fallback
        }
    }

    return remember(capturedImage) {
        capturedImage?.let { BitmapDescriptor(it) }
            ?: BitmapDescriptor(createTransparentPlaceholder())
    }
}

/**
 * Captures Compose [content] to a `UIImage` using `GraphicsLayer` inside a `ComposeUIViewController`.
 *
 * A separate `ComposeUIViewController` is created to provide an isolated composition context
 * (the caller runs inside `MapApplier` which cannot host regular UI composables).
 *
 * Cancellation is handled carefully to avoid "coroutine scope left composition" errors
 * during rapid marker addition/removal.
 *
 * @param content The composable to render off-screen and capture.
 * @return A `UIImage` containing the rasterized content, scaled to the device screen density.
 * @throws IllegalStateException if the content fails to measure or the capture times out.
 * @since 0.0.1
 */
@OptIn(ExperimentalForeignApi::class, ExperimentalComposeUiApi::class)
internal suspend fun captureComposableToUIImage(content: @Composable () -> Unit): UIImage {
    val sizeDeferred = CompletableDeferred<IntSize>()
    val captureComplete = CompletableDeferred<ImageBitmap>()

    val controller =
        ComposeUIViewController(
            configure = { opaque = false }
        ) {
            val graphicsLayer = rememberGraphicsLayer()
            var contentSize by remember { mutableStateOf(IntSize.Zero) }
            var shouldCapture by remember { mutableStateOf(false) }

            Box(
                modifier =
                    Modifier
                        .wrapContentSize(unbounded = true)
                        .drawWithContent {
                            if (shouldCapture && contentSize.width > 0 && contentSize.height > 0) {
                                graphicsLayer.record(size = contentSize) {
                                    this@drawWithContent.drawContent()
                                }
                            }
                            drawContent()
                        }
            ) {
                Box(
                    modifier =
                        Modifier.onSizeChanged { size ->
                            if (size.width > 0 && size.height > 0 && contentSize == IntSize.Zero) {
                                contentSize = size
                                sizeDeferred.complete(size)
                            }
                        }
                ) {
                    content()
                }
            }

            LaunchedEffect(shouldCapture, contentSize) {
                if (shouldCapture && contentSize.width > 0 && contentSize.height > 0) {
                    try {
                        val bitmap = graphicsLayer.toImageBitmap()
                        captureComplete.complete(bitmap)
                    } catch (e: CancellationException) {
                        // Propagate cancellation without completing exceptionally
                        throw e
                    } catch (e: Exception) {
                        captureComplete.completeExceptionally(e)
                    }
                }
            }

            LaunchedEffect(contentSize) {
                if (contentSize.width > 0 && contentSize.height > 0) {
                    kotlinx.coroutines.delay(CAPTURE_TRIGGER_DELAY_MS)
                    shouldCapture = true
                }
            }
        }

    val view = controller.view
    val scale = UIScreen.mainScreen.scale
    val initialSize = 200.0

    // keyWindow can be null during app launch, background transitions, or on iPadOS
    // multi-scene setups. When null, the view is still laid out in-memory (setNeedsLayout /
    // layoutIfNeeded work without a window), but the capture may produce an empty image
    // because UIKit skips rendering for views not in the window hierarchy. This is
    // acceptable — the caller falls back to a transparent placeholder.
    val keyWindow = platform.UIKit.UIApplication.sharedApplication.keyWindow

    try {
        if (keyWindow != null) {
            // Position off-screen so the temporary view is never visible to the user.
            view.setFrame(CGRectMake(-1000.0, -1000.0, initialSize, initialSize))
            keyWindow.addSubview(view)
        }

        // Check for cancellation before each potentially blocking operation
        coroutineContext.ensureActive()

        view.setNeedsLayout()
        view.layoutIfNeeded()
        kotlinx.coroutines.delay(LAYOUT_SETTLE_DELAY_MS)

        coroutineContext.ensureActive()

        val measuredSize =
            kotlinx.coroutines.withTimeoutOrNull(MEASURE_TIMEOUT_MS) {
                sizeDeferred.await()
            } ?: error("Composable content failed to measure within ${MEASURE_TIMEOUT_MS}ms - ensure it has non-zero intrinsic size")

        coroutineContext.ensureActive()

        val widthPoints = measuredSize.width.toDouble() / scale
        val heightPoints = measuredSize.height.toDouble() / scale
        view.setFrame(CGRectMake(-1000.0, -1000.0, widthPoints, heightPoints))
        view.setNeedsLayout()
        view.layoutIfNeeded()

        coroutineContext.ensureActive()

        val bitmap =
            kotlinx.coroutines.withTimeoutOrNull(CAPTURE_TIMEOUT_MS) {
                captureComplete.await()
            } ?: error("GraphicsLayer capture failed to complete within ${CAPTURE_TIMEOUT_MS}ms - content may not be rendering correctly")

        return bitmap.toUIImage()
    } finally {
        // Always clean up the view, even on cancellation
        // Cancel any pending operations first
        sizeDeferred.cancel()
        captureComplete.cancel()
        view.removeFromSuperview()
    }
}

/**
 * Converts this [ImageBitmap] to a `UIImage` using Skia PNG encoding.
 *
 * Reads raw pixels, transfers them to a Skia bitmap, encodes to PNG, and creates a
 * `UIImage` from the resulting `NSData`. The PNG route ensures correct alpha
 * premultiplication and color-space handling. The returned image is scaled to
 * `UIScreen.mainScreen.scale` for Retina displays.
 *
 * @return A `UIImage` at the device's native screen scale.
 * @throws IllegalArgumentException if the bitmap has non-positive dimensions.
 * @since 0.0.1
 */
@OptIn(ExperimentalForeignApi::class, kotlinx.cinterop.BetaInteropApi::class)
internal fun ImageBitmap.toUIImage(): UIImage {
    val width = this.width
    val height = this.height
    val scale = UIScreen.mainScreen.scale

    require(width > 0 && height > 0) {
        "ImageBitmap must have positive dimensions"
    }

    val buffer = IntArray(width * height)
    this.readPixels(buffer)

    val byteBuffer = ByteArray(width * height * 4)
    for (i in buffer.indices) {
        val pixel = buffer[i]
        val offset = i * 4
        byteBuffer[offset] = (pixel and 0xFF).toByte()
        byteBuffer[offset + 1] = ((pixel shr 8) and 0xFF).toByte()
        byteBuffer[offset + 2] = ((pixel shr 16) and 0xFF).toByte()
        byteBuffer[offset + 3] = ((pixel shr 24) and 0xFF).toByte()
    }

    val skiaBitmap = org.jetbrains.skia.Bitmap()
    skiaBitmap.allocN32Pixels(width, height)
    skiaBitmap.installPixels(byteBuffer)

    val skiaImage =
        org.jetbrains.skia.Image
            .makeFromBitmap(skiaBitmap)
    val pngData =
        skiaImage.encodeToData(EncodedImageFormat.PNG)
            ?: error("Failed to encode ImageBitmap to PNG")

    val nsData =
        pngData.bytes.usePinned { pinned ->
            NSData.create(bytes = pinned.addressOf(0), length = pngData.size.toULong())
        }

    val uiImage = UIImage(data = nsData)

    return if (scale != 1.0) {
        UIImage.imageWithCGImage(uiImage.CGImage, scale, uiImage.imageOrientation)
    } else {
        uiImage
    }
}

/**
 * Creates a 1x1 transparent placeholder `UIImage`.
 *
 * Used as a fallback marker icon while the actual content capture is in progress.
 *
 * @return A transparent 1x1 `UIImage` at the device's screen scale.
 * @since 0.0.1
 */
@OptIn(ExperimentalForeignApi::class)
private fun createTransparentPlaceholder(): UIImage {
    val size = CGSizeMake(1.0, 1.0)

    UIGraphicsBeginImageContextWithOptions(size, false, UIScreen.mainScreen.scale)
    try {
        val context = UIGraphicsGetCurrentContext()
        if (context != null) {
            platform.CoreGraphics.CGContextClearRect(context, CGRectMake(0.0, 0.0, 1.0, 1.0))
        }

        return requireNotNull(UIGraphicsGetImageFromCurrentImageContext()) {
            "Failed to create transparent placeholder image"
        }
    } finally {
        UIGraphicsEndImageContext()
    }
}
