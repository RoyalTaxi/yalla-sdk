package uz.yalla.primitives.button

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
import uz.yalla.primitives.button.SensitiveButtonDefaults.colors
import uz.yalla.primitives.button.SensitiveButtonDefaults.dimens
import uz.yalla.resources.Res
import uz.yalla.resources.img_sensitive_background
import kotlin.math.ceil

/**
 * Color configuration for [SensitiveButton].
 *
 * Use [SensitiveButtonDefaults.colors] to create with theme-aware defaults.
 */
@Immutable
data class SensitiveButtonColors(
    val progressColor: Color,
    val textColor: Color,
)

/**
 * Dimension configuration for [SensitiveButton].
 *
 * Use [SensitiveButtonDefaults.dimens] to create with standard values.
 */
@Immutable
data class SensitiveButtonDimens(
    val height: Dp,
    val shape: Shape,
)

/**
 * Countdown confirmation button for destructive or sensitive actions.
 *
 * Displays a countdown timer that must complete before the button becomes active.
 * A progress bar fills from left to right during countdown, overlaying a background image.
 * The button manages its own countdown animation internally.
 *
 * Use for actions like "Cancel Order", "Delete Account", or "Logout".
 *
 * ## Usage
 * ```kotlin
 * SensitiveButton(onClick = { viewModel.cancelOrder() })
 * ```
 *
 * ## With Custom Text
 * ```kotlin
 * SensitiveButton(
 *     onClick = { viewModel.deleteAccount() },
 *     countdownSeconds = 5,
 *     confirmText = "Yes, delete",
 *     countdownText = "Deleting in %s...",
 * )
 * ```
 *
 * @param onClick Called when the countdown completes and the user clicks. Not called during countdown.
 * @param confirmText Text shown when the countdown completes. Pass a localized
 *   string via `stringResource(...)` — the component carries no default so
 *   feature copy stays in the consumer.
 * @param countdownText Text shown during the countdown. Use `%s` (or `%d`) as
 *   a `formatArgs` placeholder for the remaining seconds. Pass a localized
 *   string via `stringResource(...)`.
 * @param modifier [Modifier] applied to the root container.
 * @param countdownSeconds Duration of the countdown in seconds.
 * @param colors [SensitiveButtonColors] for progress and text colors.
 *   See [SensitiveButtonDefaults.colors].
 * @param dimens [SensitiveButtonDimens] for dimensions and shape.
 *   See [SensitiveButtonDefaults.dimens].
 *
 * @see SensitiveButtonDefaults
 */
@Composable
fun SensitiveButton(
    onClick: () -> Unit,
    confirmText: String,
    countdownText: String,
    modifier: Modifier = Modifier,
    countdownSeconds: Int = 3,
    textStyle: TextStyle = System.font.body.large.bold,
    colors: SensitiveButtonColors = SensitiveButtonDefaults.colors(),
    dimens: SensitiveButtonDimens = SensitiveButtonDefaults.dimens(),
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
            progress.animateTo(1f, tween(countdownSeconds * 1000, easing = LinearEasing))
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
                    .background(colors.progressColor, dimens.shape),
            )

            Text(
                text = when {
                    isEnabled -> confirmText
                    else -> countdownText.formatArgs(countdown)
                },
                color = colors.textColor,
                style = textStyle,
            )
        }
    }
}

/**
 * Default configuration values for [SensitiveButton].
 *
 * Provides theme-aware [colors] and standard [dimens] that can be individually overridden.
 */
object SensitiveButtonDefaults {
    /** Default button height in dp. Matches [PrimaryButton] and [SecondaryButton] heights for layout consistency. */
    val Height = 60.dp
    val Shape: Shape = RoundedCornerShape(16.dp)

    /** Creates [SensitiveButtonColors] with theme-aware defaults. */
    @Composable
    fun colors(
        progressColor: Color = System.color.button.active,
        textColor: Color = System.color.text.white,
    ): SensitiveButtonColors = SensitiveButtonColors(
        progressColor = progressColor,
        textColor = textColor,
    )

    /** Creates [SensitiveButtonDimens] with standard values. */
    fun dimens(
        height: Dp = Height,
        shape: Shape = Shape,
    ): SensitiveButtonDimens = SensitiveButtonDimens(
        height = height,
        shape = shape,
    )
}

@Preview
@Composable
private fun SensitiveButtonPreview() {
    YallaTheme {
        Box(
            modifier = Modifier
                .background(Color.White)
                .padding(16.dp),
        ) {
            SensitiveButton(
                onClick = {},
                confirmText = "Yes, cancel",
                countdownText = "Hold to cancel (%s)",
            )
        }
    }
}
