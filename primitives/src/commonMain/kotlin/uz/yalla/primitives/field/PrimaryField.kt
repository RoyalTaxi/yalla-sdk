package uz.yalla.primitives.field

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * Color configuration for [PrimaryField].
 *
 * Resolves text, border, cursor, and placeholder colors based on focus state.
 * Use [PrimaryFieldDefaults.colors] to create with theme-aware defaults.
 *
 * @param focusedTextColor Text color when focused.
 * @param unfocusedTextColor Text color when not focused.
 * @param focusedBorderColor Border color when focused.
 * @param unfocusedBorderColor Border color when not focused.
 * @param cursorColor Cursor color.
 * @param selectionColors Text selection handle and highlight colors.
 * @param placeholderColor Placeholder text color.
 * @since 0.0.1
 */
@Immutable
data class PrimaryFieldColors(
    val focusedTextColor: Color,
    val unfocusedTextColor: Color,
    val focusedBorderColor: Color,
    val unfocusedBorderColor: Color,
    val cursorColor: Color,
    val selectionColors: TextSelectionColors,
    val placeholderColor: Color,
)

/**
 * Dimension configuration for [PrimaryField].
 *
 * Use [PrimaryFieldDefaults.dimens] to create with standard values.
 *
 * @param shape Corner shape of the outlined field.
 * @since 0.0.1
 */
@Immutable
data class PrimaryFieldDimens(
    val shape: Shape,
)

/**
 * Default configuration values for [PrimaryField].
 *
 * Provides theme-aware defaults for [colors], [textStyle], and [dimens].
 * @since 0.0.1
 */
object PrimaryFieldDefaults {

    /**
     * Creates theme-aware color configuration for [PrimaryField].
     *
     * @param focusedTextColor Text color when the field is focused.
     * @param unfocusedTextColor Text color when the field is not focused.
     * @param focusedBorderColor Border color when the field is focused.
     * @param unfocusedBorderColor Border color when the field is not focused.
     * @param cursorColor Color of the text cursor.
     * @param selectionColors Colors for text selection handles and highlight.
     * @param placeholderColor Color of the placeholder text.
     */
    @Composable
    fun colors(
        focusedTextColor: Color = System.color.text.base,
        unfocusedTextColor: Color = System.color.text.base,
        focusedBorderColor: Color = System.color.border.filled,
        unfocusedBorderColor: Color = System.color.border.disabled,
        cursorColor: Color = System.color.text.link,
        selectionColors: TextSelectionColors =
            TextSelectionColors(
                handleColor = System.color.text.link,
                backgroundColor = System.color.text.link.copy(.3f)
            ),
        placeholderColor: Color = System.color.text.subtle,
    ): PrimaryFieldColors = PrimaryFieldColors(
        focusedTextColor = focusedTextColor,
        unfocusedTextColor = unfocusedTextColor,
        focusedBorderColor = focusedBorderColor,
        unfocusedBorderColor = unfocusedBorderColor,
        cursorColor = cursorColor,
        selectionColors = selectionColors,
        placeholderColor = placeholderColor,
    )

    /** Creates theme-aware text style for [PrimaryField]. */
    @Composable
    fun textStyle(): TextStyle = System.font.body.base.medium

    /**
     * Creates dimension configuration for [PrimaryField].
     *
     * @param shape Corner shape of the outlined text field.
     */
    fun dimens(
        shape: Shape = RoundedCornerShape(10.dp),
    ): PrimaryFieldDimens = PrimaryFieldDimens(
        shape = shape,
    )
}

/**
 * Primary text input field with outlined style.
 *
 * Wraps Material3 [OutlinedTextField] with Yalla design system defaults.
 * Supports focus management, input filtering, visual transformations,
 * and optional leading/trailing icon slots.
 *
 * ## Usage
 *
 * ```kotlin
 * val textState = rememberTextFieldState()
 *
 * PrimaryField(
 *     state = textState,
 *     modifier = Modifier.fillMaxWidth(),
 *     placeholder = { Text("Enter your name") },
 * )
 * ```
 *
 * @param state Text field state managing the input value and selection.
 * @param modifier Applied to the root outlined text field.
 * @param focusRequester Optional focus requester for programmatic focus.
 * @param enabled Whether the field accepts input.
 * @param readOnly Whether the field is read-only (focusable but not editable).
 * @param lineLimits Line limit configuration (default single line).
 * @param keyboardOptions Keyboard type, IME action, etc.
 * @param inputTransformation Input transformation for filtering typed characters.
 * @param outputTransformation Output transformation for visual formatting.
 * @param colors Color configuration, defaults to [PrimaryFieldDefaults.colors].
 * @param textStyle Text style for input text, defaults to [PrimaryFieldDefaults.textStyle].
 * @param dimens Dimension configuration, defaults to [PrimaryFieldDefaults.dimens].
 * @param leadingIcon Optional leading icon slot.
 * @param trailingIcon Optional trailing icon slot.
 * @param placeholder Optional placeholder content shown when field is empty.
 *
 * @see NumberField for phone number input variant
 * @see SearchField for search input variant
 * @see PrimaryFieldDefaults for default values
 * @since 0.0.1
 */
@Composable
fun PrimaryField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.SingleLine,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    inputTransformation: InputTransformation? = null,
    outputTransformation: OutputTransformation? = null,
    colors: PrimaryFieldColors = PrimaryFieldDefaults.colors(),
    textStyle: TextStyle = PrimaryFieldDefaults.textStyle(),
    dimens: PrimaryFieldDimens = PrimaryFieldDefaults.dimens(),
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
) {
    OutlinedTextField(
        state = state,
        modifier = modifier.applyFocusRequester(focusRequester),
        textStyle = textStyle,
        placeholder = placeholder,
        lineLimits = lineLimits,
        shape = dimens.shape,
        inputTransformation = inputTransformation,
        outputTransformation = outputTransformation,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        enabled = enabled,
        readOnly = readOnly,
        keyboardOptions = keyboardOptions,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = colors.focusedTextColor,
            unfocusedTextColor = colors.unfocusedTextColor,
            focusedBorderColor = colors.focusedBorderColor,
            unfocusedBorderColor = colors.unfocusedBorderColor,
            cursorColor = colors.cursorColor,
            selectionColors = colors.selectionColors,
            focusedPlaceholderColor = colors.placeholderColor,
            unfocusedPlaceholderColor = colors.placeholderColor,
        ),
    )
}

private fun Modifier.applyFocusRequester(focusRequester: FocusRequester?): Modifier =
    if (focusRequester != null) this.focusRequester(focusRequester) else this
