package uz.yalla.media.picker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uz.yalla.media.config.requireMedia
import uz.yalla.media.utils.MediaScope

@Composable
public actual fun rememberImagePickerLauncher(
    selectionMode: SelectionMode,
    @Suppress("UNUSED_PARAMETER") scope: CoroutineScope,
    onResult: (List<ByteArray>) -> Unit
): ImagePickerLauncher {
    val context = LocalContext.current
    val latestOnResult by rememberUpdatedState(onResult)
    val selectionLimit = selectionMode.toSelectionLimit()
    return remember {
        ImagePickerLauncher {
            requireMedia().factory.pickImages(selectionLimit) { uris ->
                // Read on the module-owned scope, not the caller's: the OS picker can outlive the
                // composition scope, and a cancelled caller scope must not drop the picked image.
                MediaScope.launch(Dispatchers.IO) {
                    val images =
                        uris.mapNotNull { uri ->
                            context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                        }
                    withContext(Dispatchers.Main) { latestOnResult(images) }
                }
            }
        }
    }
}
