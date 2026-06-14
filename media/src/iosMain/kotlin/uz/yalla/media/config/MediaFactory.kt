package uz.yalla.media.config

import platform.Foundation.NSData

public interface MediaFactory {
    public fun pickImages(
        selectionLimit: Int,
        onResult: (List<NSData>) -> Unit
    )

    public fun captureImage(onResult: (NSData?) -> Unit)
}
