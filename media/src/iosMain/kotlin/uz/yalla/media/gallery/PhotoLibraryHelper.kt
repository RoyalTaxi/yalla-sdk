package uz.yalla.media.gallery

import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.CoreGraphics.CGSize
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSPredicate
import platform.Photos.PHAsset
import platform.Photos.PHAssetMediaTypeImage
import platform.Photos.PHAuthorizationStatusAuthorized
import platform.Photos.PHFetchOptions
import platform.Photos.PHImageContentModeAspectFill
import platform.Photos.PHImageManager
import platform.Photos.PHImageRequestOptions
import platform.Photos.PHPhotoLibrary
import platform.UIKit.UIImage

private const val THUMBNAIL_SIZE = 300.0
private const val THUMBNAIL_QUALITY = 0.8

internal data class YallaMediaAsset(
    val asset: PHAsset,
    val thumbnailBytes: ByteArray?
)

internal fun checkPhotoLibraryAuthorization(): Boolean {
    val status = PHPhotoLibrary.authorizationStatus()
    return status == PHAuthorizationStatusAuthorized
}

@OptIn(ExperimentalForeignApi::class)
internal suspend fun fetchImagesFromGallery(): List<YallaMediaAsset> =
    withContext(Dispatchers.Default) {
        val fetchOptions =
            PHFetchOptions().apply {
                predicate = NSPredicate.predicateWithFormat("mediaType = %d", PHAssetMediaTypeImage)
            }
        val photos = PHAsset.fetchAssetsWithOptions(fetchOptions)
        (0 until photos.count.toInt()).mapNotNull { index ->
            val asset = photos.objectAtIndex(index.toULong()) as? PHAsset ?: return@mapNotNull null
            val thumbnail =
                asset.getAssetThumbnail(CGSizeMake(THUMBNAIL_SIZE, THUMBNAIL_SIZE))?.toByteArray(THUMBNAIL_QUALITY)
            YallaMediaAsset(asset = asset, thumbnailBytes = thumbnail)
        }
    }

@OptIn(ExperimentalForeignApi::class)
internal suspend fun PHAsset.getFullImageByteArray(): ByteArray? {
    val fullImage = getAssetThumbnail(CGSizeMake(pixelWidth.toDouble(), pixelHeight.toDouble()))
    return fullImage?.toByteArray()
}

@OptIn(ExperimentalForeignApi::class)
internal suspend fun PHAsset.getAssetThumbnail(targetSize: CValue<CGSize>): UIImage? =
    withContext(Dispatchers.Default) {
        var image: UIImage? = null
        val options =
            PHImageRequestOptions().apply {
                setSynchronous(true)
                setNetworkAccessAllowed(true)
            }
        PHImageManager.defaultManager().requestImageForAsset(
            this@getAssetThumbnail,
            targetSize,
            PHImageContentModeAspectFill,
            options
        ) { uiImage, _ ->
            image = uiImage
        }
        image
    }
