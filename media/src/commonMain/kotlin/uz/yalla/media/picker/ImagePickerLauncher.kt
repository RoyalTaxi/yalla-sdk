package uz.yalla.media.picker

import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope

@Composable
public expect fun rememberImagePickerLauncher(
    selectionMode: SelectionMode = SelectionMode.Single,
    scope: CoroutineScope,
    onResult: (List<ByteArray>) -> Unit
): ImagePickerLauncher

public sealed class SelectionMode {
    public data object Single : SelectionMode()

    public data class Multiple(
        val maxSelection: Int = INFINITY
    ) : SelectionMode()

    public companion object {
        public const val INFINITY: Int = 0
    }
}

public class ImagePickerLauncher internal constructor(
    private val onLaunch: () -> Unit
) {
    public fun launch(): Unit = onLaunch()
}
