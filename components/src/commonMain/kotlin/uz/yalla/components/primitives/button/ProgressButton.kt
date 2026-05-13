package uz.yalla.components.primitives.button

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.resources.painterResource
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme
import uz.yalla.resources.Res
import uz.yalla.resources.img_sensitive_background

@Composable
fun ProgressButton(
    text: String,
    progress: Float,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(System.radius.l),
        enabled = enabled,
        modifier = modifier,
        onClick = onClick
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {
            Image(
                painter = painterResource(Res.drawable.img_sensitive_background),
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = Modifier.matchParentSize()
            )

            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .background(System.color.background.brand)
            )

            Text(
                text = text,
                color = System.color.text.white,
                style = System.font.body.large.bold,
                modifier = Modifier.padding(
                    PaddingValues(
                        vertical = System.space.scale.l,
                        horizontal = System.space.scale.xxl
                    )
                )
            )
        }
    }
}

@Preview
@Composable
private fun Preview() = YallaTheme {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 3000,
                easing = LinearEasing
            )
        )
    }

    ProgressButton(
        text = "Yes, cancel my trip",
        progress = progress.value,
        enabled = progress.isRunning.not(),
        onClick = {

        }
    )
}