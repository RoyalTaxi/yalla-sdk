package uz.yalla.components.primitives.button

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import org.jetbrains.compose.resources.painterResource
import uz.yalla.core.util.formatArgs
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme
import uz.yalla.resources.Res
import uz.yalla.resources.img_sensitive_background
import kotlin.math.ceil

@Immutable
public data class SensitiveButtonColors(
    val progressColor: Color,
    val textColor: Color
)

@Immutable
public data class SensitiveButtonDimens(
    val height: Dp,
    val shape: Shape
)

@Immutable
public data class SensitiveButtonStyles(
    val textStyle: TextStyle
)

public object SensitiveButtonDefaults {
    @Composable
    public fun colors(
        progressColor: Color = System.color.button.active,
        textColor: Color = System.color.text.white
    ): SensitiveButtonColors =
        SensitiveButtonColors(
            progressColor = progressColor,
            textColor = textColor
        )

    @Composable
    public fun dimens(
        height: Dp = 60.dp,
        shape: Shape = RoundedCornerShape(16.dp)
    ): SensitiveButtonDimens =
        SensitiveButtonDimens(
            height = height,
            shape = shape
        )

    @Composable
    public fun styles(textStyle: TextStyle = System.font.body.large.bold): SensitiveButtonStyles =
        SensitiveButtonStyles(
            textStyle = textStyle
        )
}

@Composable
public fun SensitiveButton(
    onClick: () -> Unit,
    confirmText: String,
    countdownText: String,
    modifier: Modifier = Modifier,
    countdownSeconds: Int = 3,
    colors: SensitiveButtonColors = SensitiveButtonDefaults.colors(),
    dimens: SensitiveButtonDimens = SensitiveButtonDefaults.dimens(),
    styles: SensitiveButtonStyles = SensitiveButtonDefaults.styles()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val progress = remember { Animatable(0f) }
    val countdown by remember {
        derivedStateOf { ceil(countdownSeconds * (1f - progress.value)).toInt() }
    }
    val isEnabled by remember { derivedStateOf { progress.value >= 1f } }

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            progress.snapTo(0f)
            progress.animateTo(
                targetValue = 1f,
                animationSpec =
                    tween(
                        durationMillis = countdownSeconds * 1000,
                        easing = LinearEasing
                    )
            )
        }
    }

    Surface(
        onClick = onClick,
        enabled = isEnabled,
        shape = dimens.shape,
        color = Color.Transparent,
        modifier = modifier.height(dimens.height)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(Res.drawable.img_sensitive_background),
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier =
                    Modifier
                        .align(Alignment.CenterStart)
                        .fillMaxHeight()
                        .fillMaxWidth(progress.value)
                        .background(colors.progressColor, dimens.shape)
            )

            Text(
                text = if (isEnabled) confirmText else countdownText.formatArgs(countdown),
                color = colors.textColor,
                style = styles.textStyle
            )
        }
    }
}

@Preview
@Composable
private fun Preview() =
    YallaTheme {
        SensitiveButton(
            onClick = { },
            confirmText = "Yes, cancel my trip",
            countdownText = "Yes, cancel my trip ({0})",
            modifier =
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
        )
    }
