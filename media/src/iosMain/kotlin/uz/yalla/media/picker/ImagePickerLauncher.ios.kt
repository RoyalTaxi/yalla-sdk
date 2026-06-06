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
import uz.yalla.media.utils.toByteArray

@Composable
actual fun rememberImagePickerLauncher(
    selectionMode: SelectionMode,
    scope: CoroutineScope,
    onResult: (List<ByteArray>) -> Unit
): ImagePickerLauncher {
    val latestOnResult by rememberUpdatedState(onResult)
    val selectionLimit = when (selectionMode) {
        SelectionMode.Single -> 1
        is SelectionMode.Multiple -> selectionMode.maxSelection
    }
    return remember {
        ImagePickerLauncher {
            requireMedia().factory.pickImages(selectionLimit) { dataList ->
                scope.launch(Dispatchers.Default) {
                    val images = dataList.map { it.toByteArray() }
                    withContext(Dispatchers.Main) { latestOnResult(images) }
                }
            }
        }
    }
}
