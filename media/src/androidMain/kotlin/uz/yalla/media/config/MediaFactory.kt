package uz.yalla.media.config

import android.net.Uri

interface MediaFactory {
    fun pickImages(selectionLimit: Int, onResult: (List<Uri>) -> Unit)

    fun captureImage(onResult: (Uri?) -> Unit)
}
