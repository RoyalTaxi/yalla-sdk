package uz.yalla.components.primitives.pin

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import org.jetbrains.compose.resources.ExperimentalResourceApi
import uz.yalla.resources.Res

private val DefaultSize = 200.dp
private const val LottieResourcePath = "files/lottie_order_search.json"

@OptIn(ExperimentalResourceApi::class)
@Composable
public fun SearchPin(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition {
        LottieCompositionSpec.JsonString(Res.readBytes(LottieResourcePath).decodeToString())
    }

    Image(
        painter = rememberLottiePainter(composition, iterations = Compottie.IterateForever),
        contentDescription = null,
        modifier = modifier.size(DefaultSize)
    )
}
