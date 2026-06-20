package uz.yalla.media.picker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uz.yalla.media.config.requireMedia
import uz.yalla.media.utils.MediaScope
import uz.yalla.media.utils.toByteArray

@Composable
public actual fun rememberImagePickerLauncher(
    selectionMode: SelectionMode,
    @Suppress("UNUSED_PARAMETER") scope: CoroutineScope,
    onResult: (List<ByteArray>) -> Unit
): ImagePickerLauncher {
    val latestOnResult by rememberUpdatedState(onResult)
    val selectionLimit = selectionMode.toSelectionLimit()
    return remember {
        ImagePickerLauncher {
            requireMedia().factory.pickImages(selectionLimit) { dataList ->
                // Read on the module-owned scope, not the caller's: the OS picker can outlive the
                // composition scope, and a cancelled caller scope must not drop the picked image.
                // MediaScope is backed by Dispatchers.Default (Dispatchers.IO is JVM-only / absent on
                // Kotlin/Native), which is the off-main background dispatcher for the byte read.
                MediaScope.launch {
                    val images = dataList.map { it.toByteArray() }
                    withContext(Dispatchers.Main) { latestOnResult(images) }
                }
            }
        }
    }
}
