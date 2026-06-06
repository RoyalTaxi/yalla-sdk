package uz.yalla.media.config

import platform.Foundation.NSData

interface MediaFactory {
    fun pickImages(selectionLimit: Int, onResult: (List<NSData>) -> Unit)

    fun captureImage(onResult: (NSData?) -> Unit)
}
