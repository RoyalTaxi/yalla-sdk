package uz.yalla.maps.compose

import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalView
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import android.graphics.Color as AndroidColor
import com.google.android.gms.maps.model.BitmapDescriptorFactory as GoogleBitmapDescriptorFactory

@Composable
@GoogleMapComposable
actual fun rememberComposeBitmapDescriptor(
    vararg keys: Any,
    content: @Composable () -> Unit,
): BitmapDescriptor {
    val parent = LocalView.current as ViewGroup
    val compositionContext = rememberCompositionContext()
    val currentContent by rememberUpdatedState(content)

    return remember(parent, compositionContext, *keys) {
        renderComposableToBitmapDescriptor(parent, compositionContext, currentContent)
    }
}

/** Unspecified measure spec used to measure the off-screen [ComposeView] at its intrinsic size. */
private val measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)

/**
 * Renders a composable off-screen and captures it into a [BitmapDescriptor].
 *
 * Attaches a temporary [ComposeView] to [parent], measures, lays out, and draws it
 * into an Android [Bitmap][android.graphics.Bitmap], then detaches the view.
 *
 * @param parent The parent [ViewGroup] for the temporary compose view.
 * @param compositionContext The composition context for the temporary composition.
 * @param content The composable to render.
 * @return A [BitmapDescriptor] containing the rasterized content.
 * @throws IllegalArgumentException if the content measures to zero size.
 * @since 0.0.1
 */
private fun renderComposableToBitmapDescriptor(
    parent: ViewGroup,
    compositionContext: CompositionContext,
    content: @Composable () -> Unit,
): BitmapDescriptor {
    val composeView =
        ComposeView(parent.context).apply {
            layoutParams =
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                )
            setBackgroundColor(AndroidColor.TRANSPARENT)
            setParentCompositionContext(compositionContext)
            setContent(content)
        }

    parent.addView(composeView)

    try {
        composeView.measure(measureSpec, measureSpec)
        require(composeView.measuredWidth > 0 && composeView.measuredHeight > 0) { "Content has zero size" }
        composeView.layout(0, 0, composeView.measuredWidth, composeView.measuredHeight)

        val bitmap = createBitmap(composeView.measuredWidth, composeView.measuredHeight)
        bitmap.eraseColor(AndroidColor.TRANSPARENT)
        bitmap.applyCanvas { composeView.draw(this) }

        return BitmapDescriptor(GoogleBitmapDescriptorFactory.fromBitmap(bitmap))
    } finally {
        parent.removeView(composeView)
    }
}
