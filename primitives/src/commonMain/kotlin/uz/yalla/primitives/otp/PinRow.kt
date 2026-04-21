package uz.yalla.primitives.otp

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * Color configuration for [PinRow].
 *
 * Resolves border and text colors based on fill and error state.
 * Use [PinRowDefaults.colors] to create with theme-aware defaults.
 *
 * @param textColor Text color for digits.
 * @param errorTextColor Text color when in error state.
 * @param filledBorderColor Border color for filled digit boxes.
 * @param emptyBorderColor Border color for empty digit boxes.
 * @param errorBorderColor Border color when in error state.
 * @since 0.0.1
 */
@Immutable
data class PinRowColors(
    val textColor: Color,
    val errorTextColor: Color,
    val filledBorderColor: Color,
    val emptyBorderColor: Color,
    val errorBorderColor: Color,
)

/**
 * Dimension configuration for [PinRow].
 *
 * Use [PinRowDefaults.dimens] to create with standard values.
 *
 * @param shape Shape of individual digit boxes.
 * @param spacing Horizontal spacing between digit boxes.
 * @param borderWidth Border width of digit boxes.
 * @since 0.0.1
 */
@Immutable
data class PinRowDimens(
    val shape: Shape,
    val spacing: Dp,
    val borderWidth: Dp,
)

/**
 * Default configuration values for [PinRow].
 *
 * Provides theme-aware defaults for [colors], [digitStyle], and [dimens].
 * @since 0.0.1
 */
object PinRowDefaults {

    /** Creates theme-aware color configuration for [PinRow]. */
    @Composable
    fun colors(
        textColor: Color = System.color.text.base,
        errorTextColor: Color = System.color.text.red,
        filledBorderColor: Color = System.color.border.filled,
        emptyBorderColor: Color = System.color.border.disabled,
        errorBorderColor: Color = System.color.text.red,
    ): PinRowColors = PinRowColors(
        textColor = textColor,
        errorTextColor = errorTextColor,
        filledBorderColor = filledBorderColor,
        emptyBorderColor = emptyBorderColor,
        errorBorderColor = errorBorderColor,
    )

    /** Creates theme-aware text style for digits in [PinRow]. */
    @Composable
    fun digitStyle(): TextStyle = System.font.title.large

    /** Creates dimension configuration for [PinRow]. */
    fun dimens(
        shape: Shape = RoundedCornerShape(12.dp),
        spacing: Dp = 8.dp,
        borderWidth: Dp = 1.dp,
    ): PinRowDimens = PinRowDimens(
        shape = shape,
        spacing = spacing,
        borderWidth = borderWidth,
    )
}

/**
 * Row of PIN input boxes for OTP/verification codes.
 *
 * Displays a row of individual digit boxes. Input is filtered to digits
 * and limited to [length]. Calls [onComplete] when all digits are entered.
 *
 * ## Usage
 *
 * ```kotlin
 * var code by remember { mutableStateOf("") }
 *
 * PinRow(
 *     value = code,
 *     onValueChange = { code = it },
 *     length = 5,
 *     onComplete = { verifyCode(it) },
 *     modifier = Modifier.fillMaxWidth(),
 * )
 * ```
 *
 * @param value Current PIN digit string.
 * @param onValueChange Invoked when the value changes (filtered to digits).
 * @param length Number of PIN digit boxes to display.
 * @param onComplete Invoked when all digits are entered.
 * @param modifier Applied to the root text field.
 * @param focusRequester Optional focus requester for programmatic focus.
 * @param isError Whether to show the error state (red borders/text).
 * @param colors Color configuration, defaults to [PinRowDefaults.colors].
 * @param digitStyle Text style for digits, defaults to [PinRowDefaults.digitStyle].
 * @param dimens Dimension configuration, defaults to [PinRowDefaults.dimens].
 *
 * @see PinRowDefaults for default values
 * @since 0.0.1
 */
@Composable
fun PinRow(
    value: String,
    onValueChange: (String) -> Unit,
    length: Int,
    onComplete: (String) -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester? = null,
    isError: Boolean = false,
    colors: PinRowColors = PinRowDefaults.colors(),
    digitStyle: TextStyle = PinRowDefaults.digitStyle(),
    dimens: PinRowDimens = PinRowDefaults.dimens(),
) {
    val shakeOffset = remember { Animatable(0f) }
    val scaleAnim = remember { Animatable(1f) }

    LaunchedEffect(isError) {
        if (!isError) return@LaunchedEffect
        repeat(3) {
            shakeOffset.animateTo(10f, tween(40))
            shakeOffset.animateTo(-10f, tween(40))
        }
        shakeOffset.animateTo(0f, tween(40))
    }

    BasicTextField(
        value = TextFieldValue(value, selection = TextRange(value.length)),
        onValueChange = {
            val filtered = it.text.filter(Char::isDigit).take(length)

            if (filtered != value) {
                onValueChange(filtered)

                if (value.length < length && filtered.length == length) {
                    onComplete(filtered)
                }
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                translationX = shakeOffset.value
                scaleX = scaleAnim.value
                scaleY = scaleAnim.value
            }.then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        decorationBox = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimens.spacing),
            ) {
                repeat(length) { index ->
                    val char = value.getOrNull(index)
                    val borderColor = when {
                        isError -> colors.errorBorderColor
                        char != null -> colors.filledBorderColor
                        else -> colors.emptyBorderColor
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .border(dimens.borderWidth, borderColor, dimens.shape),
                        contentAlignment = Alignment.Center,
                    ) {
                        char?.let {
                            Text(
                                text = it.toString(),
                                style = digitStyle,
                                color = if (isError) colors.errorTextColor else colors.textColor,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        },
    )
}
