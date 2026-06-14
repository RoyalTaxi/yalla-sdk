package uz.yalla.media.config

import android.net.Uri

public interface MediaFactory {
    public fun pickImages(selectionLimit: Int, onResult: (List<Uri>) -> Unit)

    public fun captureImage(onResult: (Uri?) -> Unit)
}
