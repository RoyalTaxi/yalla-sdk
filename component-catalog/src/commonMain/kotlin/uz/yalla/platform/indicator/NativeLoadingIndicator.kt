package uz.yalla.platform.indicator

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * Catalog-only stub of `:platform`'s `expect fun NativeLoadingIndicator`.
 *
 * Lives at the same package + signature so that primitives' commonMain imports
 * (`uz.yalla.platform.indicator.NativeLoadingIndicator`) resolve when those source
 * files are srcDir'd into the component-catalog's wasmJs compilation. NOT an
 * actual — we deliberately don't import the platform expect, so the catalog
 * compiles without including the rest of `:platform`.
 *
 * Renders a Material3 `CircularProgressIndicator`, which is the same fallback
 * used by Android's actual implementation.
 */
@Composable
fun NativeLoadingIndicator(
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    backgroundColor: Color = Color.Unspecified,
) {
    val resolvedColor = if (color == Color.Unspecified) {
        androidx.compose.material3.ProgressIndicatorDefaults.circularColor
    } else {
        color
    }
    CircularProgressIndicator(
        modifier = modifier,
        color = resolvedColor,
    )
}
