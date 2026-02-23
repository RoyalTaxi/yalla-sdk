package uz.yalla.components.primitive.pin

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import org.jetbrains.compose.resources.ExperimentalResourceApi
import uz.yalla.resources.Res

/**
 * Animated search/loading pin indicator using Lottie.
 *
 * This component displays an infinite loop Lottie animation
 * typically used to indicate search in progress on a map.
 *
 * ## Usage
 *
 * ```kotlin
 * // Default size
 * SearchPin()
 *
 * // Custom size
 * SearchPin(
 *     dimens = SearchPinDefaults.dimens(size = 150.dp),
 *     modifier = Modifier.align(Alignment.Center),
 * )
 * ```
 *
 * @param modifier Modifier applied to the image.
 * @param dimens Dimension configuration, defaults to [SearchPinDefaults.dimens].
 *
 * @see SearchPinDefaults for default values
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
fun SearchPin(
    modifier: Modifier = Modifier,
    dimens: SearchPinDefaults.SearchPinDimens = SearchPinDefaults.dimens(),
) {
    var lottieJson by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        lottieJson = Res.readBytes(dimens.lottieResourcePath).decodeToString()
    }

    lottieJson?.let { json ->
        val composition by rememberLottieComposition {
            LottieCompositionSpec.JsonString(json)
        }

        Image(
            painter =
                rememberLottiePainter(
                    composition = composition,
                    iterations = Compottie.IterateForever
                ),
            contentDescription = null,
            modifier = modifier.size(dimens.size)
        )
    }
}

/**
 * Default configuration values for [SearchPin].
 *
 * Provides defaults for [dimens] that can be overridden.
 */
object SearchPinDefaults {
    /**
     * Dimension configuration for [SearchPin].
     *
     * @param size Size of the search pin animation.
     * @param lottieResourcePath Resource path for the Lottie animation.
     */
    data class SearchPinDimens(
        val size: Dp,
        val lottieResourcePath: String
    )

    @Composable
    fun dimens(
        size: Dp = 200.dp,
        lottieResourcePath: String = "files/lottie_order_search.json"
    ) = SearchPinDimens(
        size = size,
        lottieResourcePath = lottieResourcePath
    )
}
