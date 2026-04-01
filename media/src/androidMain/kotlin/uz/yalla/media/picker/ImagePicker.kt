package uz.yalla.media.picker

import android.annotation.SuppressLint
import android.os.Build
import android.os.ext.SdkExtensions
import android.provider.MediaStore

/**
 * Checks whether the Android Photo Picker system component is available on this device.
 *
 * The system picker is guaranteed on API 33+. On API 30-32 it is available only when the
 * R Extensions SDK version is at least 2.
 *
 * @return `true` if the system Photo Picker can be used, `false` otherwise.
 * @since 0.0.1
 */
internal fun isSystemPickerAvailable(): Boolean =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        true
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        SdkExtensions.getExtensionVersion(Build.VERSION_CODES.R) >= 2
    } else {
        false
    }

/**
 * Returns the maximum number of images the system Photo Picker allows in a single selection.
 *
 * On devices with the system picker this delegates to [MediaStore.getPickImagesMaxLimit];
 * otherwise falls back to [Integer.MAX_VALUE] (effectively unlimited).
 *
 * @return Maximum selectable item count.
 * @since 0.0.1
 */
@SuppressLint("NewApi", "ClassVerificationFailure")
internal fun getMaxItems() =
    if (isSystemPickerAvailable()) {
        MediaStore.getPickImagesMaxLimit()
    } else {
        Integer.MAX_VALUE
    }
