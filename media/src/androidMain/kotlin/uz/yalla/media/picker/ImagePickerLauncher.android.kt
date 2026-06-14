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
import uz.yalla.media.picker.SelectionMode.Companion.INFINITY

@Composable
public actual fun rememberImagePickerLauncher(
    selectionMode: SelectionMode,
    scope: CoroutineScope,
    onResult: (List<ByteArray>) -> Unit
): ImagePickerLauncher {
    val context = LocalContext.current
    val latestOnResult by rememberUpdatedState(onResult)
    val selectionLimit =
        when (selectionMode) {
            SelectionMode.Single -> 1
            is SelectionMode.Multiple -> if (selectionMode.maxSelection == INFINITY) 0 else selectionMode.maxSelection
        }
    return remember {
        ImagePickerLauncher {
            requireMedia().factory.pickImages(selectionLimit) { uris ->
                scope.launch(Dispatchers.IO) {
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
