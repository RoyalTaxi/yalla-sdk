package uz.yalla.components.primitives.indicator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitViewController
import kotlinx.cinterop.ExperimentalForeignApi
import uz.yalla.components.config.requireConfig

@OptIn(ExperimentalForeignApi::class, ExperimentalComposeUiApi::class)
@Composable
public actual fun LoadingIndicator(
    color: Color,
    modifier: Modifier
) {
    val argb = if (color.isSpecified) color.toArgb().toLong() else 0L
    val handle = remember { requireConfig().loadingIndicator.create(argb) }

    LaunchedEffect(argb) {
        handle.setColor(argb)
    }

    UIKitViewController(
        factory = { handle.viewController },
        modifier = modifier,
        properties = UIKitInteropProperties(
            placedAsOverlay = true,
            isNativeAccessibilityEnabled = false
        )
    )
}
