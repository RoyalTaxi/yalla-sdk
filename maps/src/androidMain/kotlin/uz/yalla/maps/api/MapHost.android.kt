package uz.yalla.maps.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
public actual fun MapHost(controller: MapController, modifier: Modifier) {
    val androidController = controller as? AndroidMapController
        ?: error("MapController on Android must implement AndroidMapController. Got: ${controller::class.simpleName}")
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    AndroidView(
        factory = { ctx -> androidController.createView(ctx, lifecycle) },
        modifier = modifier,
        onRelease = { androidController.detach() }
    )
}
