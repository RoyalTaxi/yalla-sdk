package uz.yalla.media.picker

import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreImage.CIContext
import platform.CoreImage.CIFilter
import platform.CoreImage.CIImage
import platform.CoreImage.createCGImage
import platform.CoreImage.filterWithName
import platform.Foundation.setValue
import platform.UIKit.UIImage

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
