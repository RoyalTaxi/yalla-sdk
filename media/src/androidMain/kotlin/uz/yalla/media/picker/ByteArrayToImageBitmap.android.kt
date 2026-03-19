package uz.yalla.media.picker

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

/** Android implementation using [BitmapFactory.decodeByteArray]. @since 0.0.1 */
actual fun ByteArray.toImageBitmap(): ImageBitmap = BitmapFactory.decodeByteArray(this, 0, size).asImageBitmap()
