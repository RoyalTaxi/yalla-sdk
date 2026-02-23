package uz.yalla.media.gallery.model

import android.net.Uri

internal data class YallaMediaImage(
    val id: Long,
    val uri: Uri,
    val name: String?
)
