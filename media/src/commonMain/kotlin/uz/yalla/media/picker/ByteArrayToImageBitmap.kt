package uz.yalla.media.picker

import androidx.compose.ui.graphics.ImageBitmap

/**
 * Decodes this byte array into a Compose [ImageBitmap].
 *
 * On Android this delegates to [BitmapFactory.decodeByteArray]; on iOS it uses
 * Skia's `Image.makeFromEncoded`.
 *
 * @receiver Raw image bytes (JPEG, PNG, or any platform-decodable format).
 * @return Decoded [ImageBitmap] ready for use with `Image` composables.
 * @since 0.0.1
 */
expect fun ByteArray.toImageBitmap(): ImageBitmap
