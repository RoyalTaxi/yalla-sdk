package uz.yalla.maps.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitViewController

@Composable
public actual fun MapHost(
    controller: MapController,
    modifier: Modifier
) {
    val host = controller.platformHost.value
    val iosController =
        host as? IosMapController
            ?: error("platformHost is not an IosMapController: ${host?.let { it::class.simpleName }}")
    UIKitViewController(
        factory = { iosController.createViewController() },
        modifier = modifier
    )
}
