package uz.yalla.components.primitive.field

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * Default configuration values for [PrimaryField].
 *
 * Provides theme-aware defaults for [colors], [style], and [dimens] that can be overridden.
 */
object PrimaryFieldDefaults {
    /**
     * Color configuration for [PrimaryField].
     *
     * @param focusedText Text color when focused.
     * @param unfocusedText Text color when not focused.
     * @param focusedBorder Border color when focused.
     * @param unfocusedBorder Border color when not focused.
     * @param cursor Cursor color.
     * @param selection Text selection colors.
     * @param placeholder Placeholder text color.
     */
    data class PrimaryFieldColors(
        val focusedText: Color,
        val unfocusedText: Color,
        val focusedBorder: Color,
        val unfocusedBorder: Color,
        val cursor: Color,
        val selection: TextSelectionColors,
        val placeholder: Color
    )

    @Composable
    fun colors(
        focusedText: Color = System.color.textBase,
        unfocusedText: Color = System.color.textBase,
        focusedBorder: Color = System.color.borderFilled,
        unfocusedBorder: Color = System.color.borderDisabled,
        cursor: Color = System.color.textLink,
        selection: TextSelectionColors =
            TextSelectionColors(
                handleColor = System.color.textLink,
                backgroundColor = System.color.textLink.copy(.3f)
            ),
        placeholder: Color = System.color.textSubtle
    ) = PrimaryFieldColors(
        focusedText = focusedText,
        unfocusedText = unfocusedText,
        focusedBorder = focusedBorder,
        unfocusedBorder = unfocusedBorder,
        cursor = cursor,
        selection = selection,
        placeholder = placeholder
    )

    /**
     * Text style configuration for [PrimaryField].
     *
     * @param text Style applied to the input text.
     * @param placeholder Style applied to the placeholder text.
     */
    data class PrimaryFieldStyle(
        val text: TextStyle,
        val placeholder: TextStyle
    )

    @Composable
    fun style(
        text: TextStyle = System.font.body.base.medium,
        placeholder: TextStyle = System.font.body.base.medium
    ) = PrimaryFieldStyle(
        text = text,
        placeholder = placeholder
    )

    /**
     * Dimension configuration for [PrimaryField].
     *
     * @param shape Field shape.
     */
    data class PrimaryFieldDimens(
        val shape: Shape
    )

    @Composable
    fun dimens(shape: Shape = RoundedCornerShape(10.dp)) =
        PrimaryFieldDimens(
            shape = shape
        )
}

/**
 * Primary text input field with outlined style.
 *
 * ## Usage
 *
 * ```kotlin
 * val textState = rememberTextFieldState()
 *
 * PrimaryField(
 *     state = textState,
 *     placeholder = "Enter your name",
 *     modifier = Modifier.fillMaxWidth()
 * )
 * ```
 *
 * @param state Text field state
 * @param modifier Applied to field
 * @param focusRequester Optional focus requester
 * @param placeholder Placeholder text when empty
 * @param lineLimits Line limit configuration
 * @param inputTransformation Input transformation for formatting
 * @param leadingIcon Optional leading icon
 * @param trailingIcon Optional trailing icon
 * @param enabled Whether field is enabled
 * @param readOnly Whether field is read-only
 * @param keyboardOptions Keyboard configuration
 * @param colors Color configuration, defaults to [PrimaryFieldDefaults.colors]
 * @param style Text style configuration, defaults to [PrimaryFieldDefaults.style]
 * @param dimens Dimension configuration, defaults to [PrimaryFieldDefaults.dimens]
 */
@Composable
fun PrimaryField(
    state: TextFieldState,
    modifier: Modifier = Modifier.fillMaxWidth(),
    focusRequester: FocusRequester? = null,
    placeholder: String? = null,
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.SingleLine,
    inputTransformation: InputTransformation? = null,
    leadingIcon: (@Composable (() -> Unit))? = null,
    trailingIcon: (@Composable (() -> Unit))? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    colors: PrimaryFieldDefaults.PrimaryFieldColors = PrimaryFieldDefaults.colors(),
    style: PrimaryFieldDefaults.PrimaryFieldStyle = PrimaryFieldDefaults.style(),
    dimens: PrimaryFieldDefaults.PrimaryFieldDimens = PrimaryFieldDefaults.dimens()
) {
    OutlinedTextField(
        state = state,
        modifier = modifier.applyFocusRequester(focusRequester),
        textStyle = style.text,
        placeholder =
            placeholder?.let {
                {
                    Text(
                        text = it,
                        style = style.placeholder,
                        color = colors.placeholder,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
        lineLimits = lineLimits,
        shape = dimens.shape,
        inputTransformation = inputTransformation,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        enabled = enabled,
        readOnly = readOnly,
        keyboardOptions = keyboardOptions,
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedTextColor = colors.focusedText,
                unfocusedTextColor = colors.unfocusedText,
                focusedBorderColor = colors.focusedBorder,
                unfocusedBorderColor = colors.unfocusedBorder,
                cursorColor = colors.cursor,
                selectionColors = colors.selection,
                focusedPlaceholderColor = colors.placeholder,
                unfocusedPlaceholderColor = colors.placeholder
            )
    )
}

private fun Modifier.applyFocusRequester(focusRequester: FocusRequester?): Modifier =
    if (focusRequester != null) this.focusRequester(focusRequester) else this
