package uz.yalla.components.primitive.otp

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * State for [PinView] component.
 *
 * @property value Current digit value.
 * @property isError Whether to show error state.
 */
data class PinViewState(
    val value: Char?,
    val isError: Boolean = false
)

/**
 * Default configuration values for [PinView].
 *
 * Provides theme-aware defaults for [colors], [style], and [dimens] that can be overridden.
 */
object PinViewDefaults {
    /**
     * Color configuration for [PinView].
     *
     * @param text Text color.
     * @param errorText Text color in error state.
     * @param focusedBorder Border color when focused.
     * @param filledBorder Border color when filled.
     * @param emptyBorder Border color when empty.
     * @param errorBorder Border color in error state.
     * @param cursor Cursor color.
     */
    data class PinViewColors(
        val text: Color,
        val errorText: Color,
        val focusedBorder: Color,
        val filledBorder: Color,
        val emptyBorder: Color,
        val errorBorder: Color,
        val cursor: Color
    )

    @Composable
    fun colors(
        text: Color = System.color.textBase,
        errorText: Color = System.color.textRed,
        focusedBorder: Color = System.color.borderFilled,
        filledBorder: Color = System.color.borderFilled,
        emptyBorder: Color = System.color.borderDisabled,
        errorBorder: Color = System.color.textRed,
        cursor: Color = System.color.textBase
    ) = PinViewColors(
        text = text,
        errorText = errorText,
        focusedBorder = focusedBorder,
        filledBorder = filledBorder,
        emptyBorder = emptyBorder,
        errorBorder = errorBorder,
        cursor = cursor
    )

    /**
     * Text style configuration for [PinView].
     *
     * @param digit Style applied to the digit text.
     */
    data class PinViewStyle(
        val digit: TextStyle
    )

    @Composable
    fun style(digit: TextStyle = System.font.title.large) =
        PinViewStyle(
            digit = digit
        )

    /**
     * Dimension configuration for [PinView].
     *
     * @param shape Field shape.
     */
    data class PinViewDimens(
        val shape: Shape
    )

    @Composable
    fun dimens(shape: Shape = RoundedCornerShape(12.dp)) =
        PinViewDimens(
            shape = shape
        )
}

/**
 * Single PIN digit input field.
 *
 * ## Usage
 *
 * ```kotlin
 * PinView(
 *     state = PinViewState(value = digit, isError = hasError),
 *     onValueChange = { updateDigit(it) },
 *     onBackspace = { focusPreviousField() }
 * )
 * ```
 *
 * @param state Pin state containing value and isError.
 * @param onValueChange Invoked when digit changes.
 * @param onBackspace Invoked when backspace is pressed on empty field.
 * @param modifier Applied to text field.
 * @param colors Color configuration, defaults to [PinViewDefaults.colors].
 * @param style Text style configuration, defaults to [PinViewDefaults.style].
 * @param dimens Dimension configuration, defaults to [PinViewDefaults.dimens].
 */
@Composable
fun PinView(
    state: PinViewState,
    onValueChange: (Char?) -> Unit,
    onBackspace: () -> Unit,
    modifier: Modifier = Modifier,
    colors: PinViewDefaults.PinViewColors = PinViewDefaults.colors(),
    style: PinViewDefaults.PinViewStyle = PinViewDefaults.style(),
    dimens: PinViewDefaults.PinViewDimens = PinViewDefaults.dimens()
) {
    var internalValue by remember { mutableStateOf(state.value?.toString().orEmpty()) }

    OutlinedTextField(
        value =
            TextFieldValue(
                text = internalValue,
                selection = TextRange(internalValue.length)
            ),
        onValueChange = { newValue ->
            if (newValue.text.length < internalValue.length) {
                onBackspace()
            }

            val digit = newValue.text.lastOrNull { it.isDigit() }
            internalValue = digit?.toString().orEmpty()
            onValueChange(digit)
        },
        shape = dimens.shape,
        modifier =
            modifier
                .aspectRatio(1f)
                .onKeyEvent { event ->
                    val isBackspace = event.key == Key.Backspace
                    val isPress = event.type == KeyEventType.KeyDown
                    if (isPress && isBackspace && internalValue.isEmpty()) {
                        onBackspace()
                        true
                    } else {
                        false
                    }
                },
        singleLine = true,
        isError = state.isError,
        textStyle = style.digit.copy(textAlign = TextAlign.Center),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                errorContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedBorderColor = colors.focusedBorder,
                unfocusedBorderColor = if (state.value == null) colors.emptyBorder else colors.filledBorder,
                errorBorderColor = colors.errorBorder,
                disabledBorderColor = colors.emptyBorder,
                focusedTextColor = colors.text,
                unfocusedTextColor = colors.text,
                errorTextColor = colors.errorText,
                disabledTextColor = colors.text,
                cursorColor = colors.cursor
            )
    )
}
