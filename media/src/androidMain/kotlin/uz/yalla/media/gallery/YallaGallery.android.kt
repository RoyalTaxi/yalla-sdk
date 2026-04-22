package uz.yalla.media.gallery

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Android implementation of [YallaGallery].
 *
 * Delegates to the Android system photo picker (`ActivityResultContracts.PickVisualMedia`),
 * which presents a modal single-image picker with its own permission flow — no storage
 * permission is requested at runtime. The selected image is returned as JPEG bytes.
 *
 * For a rich in-app Paging 3 grid, use [YallaGalleryPagingGrid] (Android-only).
 *
 * @since 0.0.1
 */
@Composable
actual fun YallaGallery(
    modifier: Modifier,
    onImageSelected: (ByteArray?) -> Unit,
) {
    val context = LocalContext.current
    // null = not yet resolved, Unit = resolved to null (cancelled), Uri = resolved to selection
    var pickedUri by remember { mutableStateOf<Any?>(null) }
    var launched by remember { mutableStateOf(false) }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            pickedUri = uri ?: Unit
        }

    LaunchedEffect(pickedUri) {
        val result = pickedUri
        when {
            result is Uri -> {
                val bytes = withContext(Dispatchers.IO) { getOriginalImageByteArray(context, result) }
                onImageSelected(bytes)
            }
            result === Unit -> onImageSelected(null)
        }
    }

    if (!launched) {
        launched = true
        LaunchedEffect(Unit) {
            launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }
}
