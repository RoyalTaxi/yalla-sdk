package uz.yalla.media.picker

import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreImage.CIContext
import platform.CoreImage.CIFilter
import platform.CoreImage.CIImage
import platform.CoreImage.createCGImage
import platform.CoreImage.filterWithName
import platform.Foundation.setValue
import platform.UIKit.UIImage

/**
 * Applies a Core Image filter to [image] based on [filterOptions].
 *
 * Returns the original [image] unchanged when [FilterOptions.Default] is specified or
 * when the Core Image pipeline fails (e.g. filter not found, CGImage unavailable).
 *
 * @param image         Source [UIImage] to filter.
 * @param filterOptions Desired color filter to apply.
 * @return A new filtered [UIImage], or the original [image] when no filter is needed.
 * @since 0.0.1
 */
@OptIn(ExperimentalForeignApi::class)
internal fun applyFilterToUIImage(
    image: UIImage,
    filterOptions: FilterOptions
): UIImage {
    val ciImage = CIImage.imageWithCGImage(image.CGImage)

    val filteredCIImage =
        when (filterOptions) {
            FilterOptions.GrayScale ->
                CIFilter
                    .filterWithName("CIPhotoEffectNoir")
                    ?.apply {
                        setValue(ciImage, forKey = "inputImage")
                    }?.outputImage

            FilterOptions.Sepia ->
                CIFilter
                    .filterWithName("CISepiaTone")
                    ?.apply {
                        setValue(ciImage, forKey = "inputImage")
                        setValue(0.8, forKey = "inputIntensity")
                    }?.outputImage

            FilterOptions.Invert ->
                CIFilter
                    .filterWithName("CIColorInvert")
                    ?.apply {
                        setValue(ciImage, forKey = "inputImage")
                    }?.outputImage

            FilterOptions.Default -> ciImage
        }

    val context = CIContext.contextWithOptions(null)
    return filteredCIImage?.let {
        val filteredCGImage = context.createCGImage(it, fromRect = it.extent())
        UIImage.imageWithCGImage(filteredCGImage)
    } ?: image
}
