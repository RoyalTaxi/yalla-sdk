package uz.yalla.media.gallery.util

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.os.Build
import android.provider.MediaStore
import androidx.core.os.bundleOf
import uz.yalla.media.gallery.model.YallaMediaImage

/**
 * MediaStore projection columns used for gallery image queries.
 *
 * Includes the row ID, display name, date taken, and album (bucket) name.
 */
private val projection =
    arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.DATE_TAKEN,
        MediaStore.Images.Media.BUCKET_DISPLAY_NAME
    )

/**
 * Creates a [Cursor] over the device's external images, sorted by date added (newest first).
 *
 * On API 29+ uses `ContentResolver.query` with a [bundleOf]-based arguments bundle;
 * on older APIs falls back to a raw SQL `ORDER BY ... LIMIT ... OFFSET ...` clause.
 *
 * @param limit  Maximum number of rows to return.
 * @param offset Number of rows to skip before the first result.
 * @return A cursor positioned before the first row, or `null` on failure.
 * @since 0.0.1
 */
internal fun Context.createCursor(
    limit: Int,
    offset: Int
): Cursor? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val bundle =
            bundleOf(
                ContentResolver.QUERY_ARG_OFFSET to offset,
                ContentResolver.QUERY_ARG_LIMIT to limit,
                ContentResolver.QUERY_ARG_SORT_COLUMNS to arrayOf(MediaStore.Images.Media.DATE_ADDED),
                ContentResolver.QUERY_ARG_SORT_DIRECTION to ContentResolver.QUERY_SORT_DIRECTION_DESCENDING
            )
        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            bundle,
            null
        )
    } else {
        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            "${MediaStore.Images.Media.DATE_ADDED} DESC LIMIT $limit OFFSET $offset",
            null
        )
    }

/**
 * Fetches a page of gallery images from the device media store.
 *
 * Opens a [Cursor] via [createCursor], iterates over the rows, and maps each one to a
 * [YallaMediaImage] containing the row ID, content URI, and display name.
 *
 * @param limit  Maximum number of images to return.
 * @param offset Number of images to skip (for pagination).
 * @return List of [YallaMediaImage] items; empty if the cursor is `null` or yields no rows.
 * @since 0.0.1
 */
internal fun Context.fetchPagePicture(
    limit: Int,
    offset: Int
): List<YallaMediaImage> {
    val pictures = ArrayList<YallaMediaImage>()
    val cursor = createCursor(limit, offset)
    cursor?.use {
        val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        val displayNameColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)

        while (it.moveToNext()) {
            val id = it.getLong(idColumn)
            val displayName = it.getString(displayNameColumn)
            val contentUri =
                ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )

            pictures.add(
                YallaMediaImage(
                    id = id,
                    uri = contentUri,
                    name = displayName
                )
            )
        }
    }
    // No manual cursor?.close() needed — cursor?.use {} already closes it.
    return pictures
}
