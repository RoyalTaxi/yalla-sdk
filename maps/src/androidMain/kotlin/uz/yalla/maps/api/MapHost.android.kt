package uz.yalla.maps.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
public actual fun MapHost(
    controller: MapController,
    modifier: Modifier
) {
    val host = controller.platformHost.value
    val androidController =
        host as? AndroidMapController
            ?: error("platformHost is not an AndroidMapController: ${host?.let { it::class.simpleName }}")
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    AndroidView(
        factory = { ctx -> androidController.createView(ctx, lifecycle) },
        modifier = modifier,
        onRelease = { androidController.detach() }
    )
}
