package uz.yalla.media

import androidx.core.content.FileProvider
import uz.yalla.sdk.media.R

/**
 * [FileProvider] subclass used to share camera-captured image files with external apps.
 *
 * Declared in the Android manifest so that temporary camera image URIs can be created
 * via `FileProvider.getUriForFile`. The provider paths are defined in `res/xml/file_paths.xml`.
 *
 * @since 0.0.1
 */
class ImageViewerFileProvider : FileProvider(R.xml.file_paths)
