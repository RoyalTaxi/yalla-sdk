package uz.yalla.maps.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitViewController

@Composable
actual fun MapHost(controller: MapController, modifier: Modifier) {
    val iosController = controller as? IosMapController
        ?: error("MapController on iOS must implement IosMapController. Got: ${controller::class.simpleName}")
    UIKitViewController(
        factory = { iosController.createViewController() },
        modifier = modifier
    )
}
