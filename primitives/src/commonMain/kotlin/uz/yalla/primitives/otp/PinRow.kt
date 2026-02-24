package uz.yalla.primitives.otp

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * State for [PinRow].
 *
 * @property length Number of PIN digits.
 * @property value Current PIN value.
 * @property isError Whether to show error state.
 */
data class PinRowState(
    val length: Int,
    val value: String,
    val isError: Boolean = false,
)

/**
 * Default configuration values for [PinRow].
 *
 * Provides theme-aware defaults for [colors], [style], and [dimens] that can be overridden.
 */
object PinRowDefaults {
    /**
     * Color configuration for [PinRow].
     *
     * @param text Text color.
     * @param errorText Text color in error state.
     * @param filledBorder Border color when digit is filled.
     * @param emptyBorder Border color when digit is empty.
     * @param errorBorder Border color in error state.
     */
    data class PinRowColors(
        val text: Color,
        val errorText: Color,
        val filledBorder: Color,
        val emptyBorder: Color,
        val errorBorder: Color
    )

    @Composable
    fun colors(
        text: Color = System.color.textBase,
        errorText: Color = System.color.textRed,
        filledBorder: Color = System.color.borderFilled,
        emptyBorder: Color = System.color.borderDisabled,
        errorBorder: Color = System.color.textRed
    ) = PinRowColors(
        text = text,
        errorText = errorText,
        filledBorder = filledBorder,
        emptyBorder = emptyBorder,
        errorBorder = errorBorder
    )

    /**
     * Text style configuration for [PinRow].
     *
     * @param digit Style applied to the digit text.
     */
    data class PinRowStyle(
        val digit: TextStyle
    )

    @Composable
    fun style(digit: TextStyle = System.font.title.large) =
        PinRowStyle(
            digit = digit
        )

    /**
     * Dimension configuration for [PinRow].
     *
     * @param shape Shape of individual digit boxes.
     * @param spacing Spacing between digit boxes.
     * @param borderWidth Border width of digit boxes.
     */
    data class PinRowDimens(
        val shape: Shape,
        val spacing: Dp,
        val borderWidth: Dp
    )

    @Composable
    fun dimens(
        shape: Shape = RoundedCornerShape(12.dp),
        spacing: Dp = 8.dp,
        borderWidth: Dp = 1.dp
    ) = PinRowDimens(
        shape = shape,
        spacing = spacing,
        borderWidth = borderWidth
    )
}

/**
 * Row of PIN input boxes for OTP/verification codes.
 *
 * ## Usage
 *
 * ```kotlin
 * var code by remember { mutableStateOf("") }
 *
 * PinRow(
 *     state = PinRowState(
 *         length = 4,
 *         value = code,
 *         isError = hasError,
 *     ),
 *     onValueChange = { code = it },
 *     onComplete = { verifyCode(it) },
 * )
 * ```
 *
 * @param state PIN input state with length, value, and error status.
 * @param onComplete Invoked when all digits are entered.
 * @param onValueChange Invoked when value changes.
 * @param modifier Applied to text field.
 * @param focusRequester Optional focus requester.
 * @param colors Color configuration, defaults to [PinRowDefaults.colors].
 * @param style Text style configuration, defaults to [PinRowDefaults.style].
 * @param dimens Dimension configuration, defaults to [PinRowDefaults.dimens].
 *
 * @see PinRowState for state configuration
 * @see PinRowDefaults for default values
 */
@Composable
fun PinRow(
    state: PinRowState,
    onComplete: (String) -> Unit,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester? = null,
    colors: PinRowDefaults.PinRowColors = PinRowDefaults.colors(),
    style: PinRowDefaults.PinRowStyle = PinRowDefaults.style(),
    dimens: PinRowDefaults.PinRowDimens = PinRowDefaults.dimens(),
) {
    BasicTextField(
        value = TextFieldValue(state.value, selection = TextRange(state.value.length)),
        onValueChange = {
            val filtered = it.text.filter(Char::isDigit).take(state.length)

            if (filtered != state.value) {
                onValueChange(filtered)

                if (state.value.length < state.length && filtered.length == state.length) {
                    onComplete(filtered)
                }
            }
        },
        modifier =
            modifier
                .fillMaxWidth()
                .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        decorationBox = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimens.spacing)
            ) {
                repeat(state.length) { index ->
                    val char = state.value.getOrNull(index)
                    val borderColor =
                        when {
                            state.isError -> colors.errorBorder
                            char != null -> colors.filledBorder
                            else -> colors.emptyBorder
                        }

                    Box(
                        modifier =
                            Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .border(dimens.borderWidth, borderColor, dimens.shape),
                        contentAlignment = Alignment.Center
                    ) {
                        char?.let {
                            Text(
                                text = it.toString(),
                                style = style.digit,
                                color = if (state.isError) colors.errorText else colors.text,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    )
}
