package uz.yalla.media.camera

import android.content.Context
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.withContext

/**
 * Asynchronously loads the CameraX [ProcessCameraProvider] and exposes it as Compose [State].
 *
 * The provider is resolved on [Dispatchers.IO] and emitted to the returned state on the
 * main thread. Returns `null` until loading completes.
 *
 * @return State holding the [ProcessCameraProvider], or `null` while loading.
 */
@Composable
fun loadCameraProvider(context: Context): State<ProcessCameraProvider?> =
    produceState(null, context) {
        value =
            withContext(Dispatchers.IO) {
                ProcessCameraProvider.getInstance(context).await()
            }
    }
