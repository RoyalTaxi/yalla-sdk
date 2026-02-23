package uz.yalla.components.primitive.button

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import uz.yalla.components.util.formatArgs
import uz.yalla.design.theme.System
import uz.yalla.resources.Res
import uz.yalla.resources.img_sensitive_background
import uz.yalla.resources.order_cancel_action_yes
import uz.yalla.resources.order_cancel_countdown

/**
 * State for [SensitiveButton] component.
 *
 * @property countdownText Text shown during countdown (with %s placeholder for seconds).
 * @property confirmText Text shown when countdown completes.
 */
data class SensitiveButtonState(
    val countdownText: String,
    val confirmText: String,
)

/**
 * Default configuration values for [SensitiveButton].
 *
 * Provides theme-aware defaults for [colors], [style], and [dimens] that can be overridden.
 */
object SensitiveButtonDefaults {
    /**
     * Color configuration for [SensitiveButton].
     *
     * @param progress Progress fill color.
     * @param text Text color.
     */
    data class SensitiveButtonColors(
        val progress: Color,
        val text: Color,
    )

    @Composable
    fun colors(
        progress: Color = System.color.buttonActive,
        text: Color = System.color.textWhite,
    ) = SensitiveButtonColors(
        progress = progress,
        text = text,
    )

    /**
     * Text style configuration for [SensitiveButton].
     *
     * @param text Button text style.
     */
    data class SensitiveButtonStyle(
        val text: TextStyle,
    )

    @Composable
    fun style(text: TextStyle = System.font.body.large.bold) =
        SensitiveButtonStyle(
            text = text,
        )

    /**
     * Dimension configuration for [SensitiveButton].
     *
     * @param height Button height.
     * @param shape Button shape.
     * @param countdownSeconds Countdown duration in seconds.
     */
    data class SensitiveButtonDimens(
        val height: Dp,
        val shape: Shape,
        val countdownSeconds: Int,
    )

    @Composable
    fun dimens(
        height: Dp = 60.dp,
        shape: Shape = RoundedCornerShape(16.dp),
        countdownSeconds: Int = 3,
    ) = SensitiveButtonDimens(
        height = height,
        shape = shape,
        countdownSeconds = countdownSeconds,
    )
}

/**
 * Countdown confirmation button for destructive or sensitive actions.
 *
 * Displays a countdown timer that must complete before the button becomes active.
 * The progress bar fills from left to right during countdown.
 *
 * Use for actions like "Delete Account", "Cancel Order", or "Logout".
 *
 * ## Usage
 *
 * ```kotlin
 * SensitiveButton(
 *     state = SensitiveButtonState(
 *         countdownText = "Hold to cancel (%s)",
 *         confirmText = "Yes, cancel",
 *     ),
 *     onClick = viewModel::cancelOrder,
 * )
 * ```
 *
 * @param state Button state containing countdown and confirm text.
 * @param onClick Invoked when countdown completes and button is clicked.
 * @param modifier Applied to button.
 * @param colors Color configuration, defaults to [SensitiveButtonDefaults.colors].
 * @param style Text style configuration, defaults to [SensitiveButtonDefaults.style].
 * @param dimens Dimension configuration, defaults to [SensitiveButtonDefaults.dimens].
 *
 * @see SensitiveButtonDefaults for default values
 */
@Composable
fun SensitiveButton(
    state: SensitiveButtonState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: SensitiveButtonDefaults.SensitiveButtonColors = SensitiveButtonDefaults.colors(),
    style: SensitiveButtonDefaults.SensitiveButtonStyle = SensitiveButtonDefaults.style(),
    dimens: SensitiveButtonDefaults.SensitiveButtonDimens = SensitiveButtonDefaults.dimens(),
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val progress = remember { Animatable(0f) }
    val countdown by remember { derivedStateOf { (dimens.countdownSeconds * (1f - progress.value)).toInt() } }
    val isEnabled by remember { derivedStateOf { progress.value >= 1f } }

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            progress.snapTo(0f)
            progress.animateTo(1f, tween(dimens.countdownSeconds * 1000, easing = LinearEasing))
        }
    }

    Surface(
        onClick = onClick,
        enabled = isEnabled,
        shape = dimens.shape,
        color = Color.Transparent,
        modifier = modifier.height(dimens.height),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(Res.drawable.img_sensitive_background),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )

            Box(
                Modifier
                    .fillMaxWidth(progress.value)
                    .height(dimens.height)
                    .align(Alignment.CenterStart)
                    .background(colors.progress, dimens.shape),
            )

            Text(
                text =
                    when {
                        isEnabled -> state.confirmText
                        else -> state.countdownText.formatArgs(countdown)
                    },
                color = colors.text,
                style = style.text,
            )
        }
    }
}

/**
 * Convenience overload using string resources.
 */
@Composable
fun SensitiveButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: SensitiveButtonDefaults.SensitiveButtonColors = SensitiveButtonDefaults.colors(),
    style: SensitiveButtonDefaults.SensitiveButtonStyle = SensitiveButtonDefaults.style(),
    dimens: SensitiveButtonDefaults.SensitiveButtonDimens = SensitiveButtonDefaults.dimens(),
) {
    SensitiveButton(
        state =
            SensitiveButtonState(
                countdownText = stringResource(Res.string.order_cancel_countdown),
                confirmText = stringResource(Res.string.order_cancel_action_yes),
            ),
        onClick = onClick,
        modifier = modifier,
        colors = colors,
        style = style,
        dimens = dimens,
    )
}

@Preview
@Composable
private fun SensitiveButtonPreview() {
    Box(
        modifier =
            Modifier
                .background(Color.White)
                .padding(16.dp)
    ) {
        SensitiveButton(
            state =
                SensitiveButtonState(
                    countdownText = "Hold to cancel (%s)",
                    confirmText = "Yes, cancel",
                ),
            onClick = {},
        )
    }
}
