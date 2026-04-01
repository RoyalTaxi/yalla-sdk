package uz.yalla.media.picker

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint

/**
 * Applies a color filter to [originalBitmap] based on [filterOptions].
 *
 * Returns the original bitmap unchanged when [FilterOptions.Default] is specified.
 * Otherwise a **new** [Bitmap] is created with the filter applied.
 *
 * **Ownership note:** the caller is responsible for recycling both the input and the
 * returned bitmap when they are no longer needed. This function never recycles
 * [originalBitmap] because the caller may still reference it (e.g. for undo).
 *
 * @param originalBitmap Source bitmap to filter. Must not be recycled.
 * @param filterOptions  Desired color filter to apply.
 * @return A new filtered [Bitmap], or [originalBitmap] itself when no filter is needed.
 * @since 0.0.1
 */
internal fun applyFilter(
    originalBitmap: Bitmap,
    filterOptions: FilterOptions
): Bitmap {
    val colorMatrix =
        when (filterOptions) {
            FilterOptions.Default -> return originalBitmap
            FilterOptions.GrayScale -> ColorMatrix().apply { setSaturation(0f) }
            FilterOptions.Sepia -> createSepiaMatrix()
            FilterOptions.Invert -> createInvertMatrix()
        }

    val paint = Paint().apply { colorFilter = ColorMatrixColorFilter(colorMatrix) }

    return Bitmap.createBitmap(originalBitmap.width, originalBitmap.height, Bitmap.Config.ARGB_8888).also {
        Canvas(it).drawBitmap(originalBitmap, 0f, 0f, paint)
    }
}

/**
 * Creates a [ColorMatrix] that produces a sepia tone effect.
 *
 * @return Sepia-toned color matrix.
 * @since 0.0.1
 */
private fun createSepiaMatrix() =
    ColorMatrix().apply {
        setSaturation(0f)
        postConcat(ColorMatrix().apply { setScale(1f, 0.95f, 0.82f, 1f) })
    }

/**
 * Creates a [ColorMatrix] that inverts all color channels.
 *
 * @return Inverted color matrix.
 * @since 0.0.1
 */
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
