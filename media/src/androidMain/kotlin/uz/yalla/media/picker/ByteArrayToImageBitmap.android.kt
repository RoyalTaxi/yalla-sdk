package uz.yalla.media.picker

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

/**
 * Android implementation of [ByteArray.toImageBitmap].
 *
 * Decodes raw image bytes via [BitmapFactory.decodeByteArray] and wraps the resulting
 * Android [android.graphics.Bitmap] as a Compose [ImageBitmap].
 *
 * @since 0.0.1
 */
actual fun ByteArray.toImageBitmap(): ImageBitmap = BitmapFactory.decodeByteArray(this, 0, size).asImageBitmap()
