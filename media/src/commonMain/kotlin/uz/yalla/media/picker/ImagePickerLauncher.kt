package uz.yalla.media.picker

import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope
import uz.yalla.media.picker.SelectionMode.Companion.INFINITY

/**
 * Remembers a handle that launches the system photo picker. [onResult] is delivered on the main
 * thread with the picked images' bytes, or an empty list on cancel. The bytes are the picker's raw
 * originals (EXIF metadata, including GPS, is preserved) — run them through
 * [uz.yalla.media.util.compressImage] before uploading to strip metadata and bound the size.
 *
 * @param selectionMode how many images the user may pick; see [SelectionMode].
 * @param scope a coroutine scope used to read the picked bytes; results are not lost if it is
 *   cancelled while the OS picker is still open (the read uses a module-owned scope).
 * @param onResult invoked on the main thread with the picked images' bytes.
 */
@Composable
public expect fun rememberImagePickerLauncher(
    selectionMode: SelectionMode = SelectionMode.Single,
    scope: CoroutineScope,
    onResult: (List<ByteArray>) -> Unit
): ImagePickerLauncher

/** How many images the user may select in the picker. */
public sealed class SelectionMode {
    /** Exactly one image. */
    public data object Single : SelectionMode()

    /**
     * Up to [maxSelection] images, or the system maximum when [maxSelection] is [INFINITY].
     *
     * @property maxSelection the cap, or [INFINITY] for the system maximum.
     */
    public data class Multiple(
        val maxSelection: Int = INFINITY
    ) : SelectionMode()

    public companion object {
        /** Sentinel for [Multiple.maxSelection] meaning "use the system maximum". */
        public const val INFINITY: Int = 0
    }
}

/**
 * Translates a [SelectionMode] into the integer selection limit the native [uz.yalla.media.config.MediaFactory]
 * expects: `1` for a single pick, `0` for unlimited ([SelectionMode.INFINITY]), or the explicit cap.
 * Expressed once here so both platform actuals share one mapping.
 */
internal fun SelectionMode.toSelectionLimit(): Int =
    when (this) {
        SelectionMode.Single -> 1
        is SelectionMode.Multiple ->
            if (maxSelection == SelectionMode.INFINITY) 0 else maxSelection
    }

/** Handle returned by [rememberImagePickerLauncher]; call [launch] to present the picker. */
public class ImagePickerLauncher internal constructor(
    private val onLaunch: () -> Unit
) {
    /** Presents the system photo picker. */
    public fun launch(): Unit = onLaunch()
}
