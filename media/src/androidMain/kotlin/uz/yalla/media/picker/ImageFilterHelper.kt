package uz.yalla.media.picker

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint

internal fun applyFilter(
    originalBitmap: Bitmap,
    filterOptions: FilterOptions
): Bitmap {
    if (filterOptions == FilterOptions.Default) return originalBitmap

    val colorMatrix =
        when (filterOptions) {
            FilterOptions.GrayScale -> ColorMatrix().apply { setSaturation(0f) }
            FilterOptions.Sepia -> createSepiaMatrix()
            FilterOptions.Invert -> createInvertMatrix()
            FilterOptions.Default -> return originalBitmap
        }

    val paint = Paint().apply { colorFilter = ColorMatrixColorFilter(colorMatrix) }

    return Bitmap.createBitmap(originalBitmap.width, originalBitmap.height, Bitmap.Config.ARGB_8888).also {
        Canvas(it).drawBitmap(originalBitmap, 0f, 0f, paint)
    }
}

private fun createSepiaMatrix() =
    ColorMatrix().apply {
        setSaturation(0f)
        postConcat(ColorMatrix().apply { setScale(1f, 0.95f, 0.82f, 1f) })
    }

private fun createInvertMatrix() =
    ColorMatrix(
        floatArrayOf(
            -1f,
            0f,
            0f,
            0f,
            255f,
            0f,
            -1f,
            0f,
            0f,
            255f,
            0f,
            0f,
            -1f,
            0f,
            255f,
            0f,
            0f,
            0f,
            1f,
            0f
        )
    )
