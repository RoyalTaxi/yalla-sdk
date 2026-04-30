package uz.yalla.media.gallery.model

import android.net.Uri

/**
 * Lightweight representation of a single image in the device media store.
 *
 * Used internally by the gallery paging pipeline to carry the minimum information
 * needed for thumbnail loading and full-size retrieval.
 */
internal data class YallaMediaImage(
    val id: Long,
    val uri: Uri,
    val name: String?
)
