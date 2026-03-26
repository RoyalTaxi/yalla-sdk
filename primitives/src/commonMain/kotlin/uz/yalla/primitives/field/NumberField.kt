package uz.yalla.primitives.field

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import uz.yalla.design.theme.System
import uz.yalla.resources.Res
import uz.yalla.resources.auth_phone_country_code
import uz.yalla.resources.auth_phone_placeholder

private const val MAX_PHONE_DIGITS = 9

/**
 * Color configuration for [NumberField].
 *
 * Resolves border color based on focus state.
 * Use [NumberFieldDefaults.colors] to create with theme-aware defaults.
 *
 * @param containerColor Background color.
 * @param textColor Input text color.
 * @param placeholderColor Placeholder text color.
 * @param prefixColor Country code text color.
 * @param dividerColor Vertical divider color between prefix and input.
 * @param focusedBorderColor Border color when focused.
 * @param unfocusedBorderColor Border color when not focused.
 * @param cursorColor Cursor color.
 * @param selectionColors Text selection handle and highlight colors.
 */
@Immutable
data class NumberFieldColors(
    val containerColor: Color,
    val textColor: Color,
    val placeholderColor: Color,
    val prefixColor: Color,
    val dividerColor: Color,
    val focusedBorderColor: Color,
    val unfocusedBorderColor: Color,
    val cursorColor: Color,
    val selectionColors: TextSelectionColors,
)

/**
 * Dimension configuration for [NumberField].
 *
 * Use [NumberFieldDefaults.dimens] to create with standard values.
 *
 * @param shape Corner shape of the container.
 * @param borderWidth Border width.
 * @param dividerThickness Thickness of the vertical divider.
 */
@Immutable
data class NumberFieldDimens(
    val shape: Shape,
    val borderWidth: Dp,
    val dividerThickness: Dp,
)

/**
 * Default configuration values for [NumberField].
 *
 * Provides theme-aware defaults for [colors], [textStyle], and [dimens].
 * @since 0.0.1
 */
object NumberFieldDefaults {

    /** Creates theme-aware color configuration for [NumberField]. */
    @Composable
    fun colors(
        containerColor: Color = System.color.background.base,
        textColor: Color = System.color.text.base,
        placeholderColor: Color = System.color.text.subtle,
        prefixColor: Color = System.color.text.base,
        dividerColor: Color = System.color.border.disabled,
        focusedBorderColor: Color = System.color.border.filled,
        unfocusedBorderColor: Color = System.color.border.disabled,
        cursorColor: Color = System.color.text.link,
        selectionColors: TextSelectionColors =
            TextSelectionColors(
                handleColor = System.color.text.link,
                backgroundColor = System.color.text.link.copy(.3f)
            ),
    ): NumberFieldColors = NumberFieldColors(
        containerColor = containerColor,
        textColor = textColor,
        placeholderColor = placeholderColor,
        prefixColor = prefixColor,
        dividerColor = dividerColor,
        focusedBorderColor = focusedBorderColor,
        unfocusedBorderColor = unfocusedBorderColor,
        cursorColor = cursorColor,
        selectionColors = selectionColors,
    )

    /** Creates theme-aware text style for [NumberField]. */
    @Composable
    fun textStyle(): TextStyle = System.font.body.base.medium

    /** Creates dimension configuration for [NumberField]. */
    fun dimens(
        shape: Shape = RoundedCornerShape(10.dp),
        borderWidth: Dp = 1.dp,
        dividerThickness: Dp = 1.dp,
    ): NumberFieldDimens = NumberFieldDimens(
        shape = shape,
        borderWidth = borderWidth,
        dividerThickness = dividerThickness,
    )
}

/**
 * Phone number input field with country code prefix and formatted input.
 *
 * Displays a bordered surface with a fixed country code on the left and
 * a formatted phone number input on the right. Input is automatically
 * filtered to digits and limited to [MAX_PHONE_DIGITS].
 *
 * ## Usage
 *
 * ```kotlin
 * var phone by remember { mutableStateOf("") }
 *
 * NumberField(
 *     value = phone,
 *     onValueChange = { phone = it },
 *     modifier = Modifier.fillMaxWidth(),
 * )
 * ```
 *
 * @param value Current raw digit value (without formatting).
 * @param onValueChange Called with filtered digits when input changes.
 * @param modifier Applied to the field container.
 * @param focusRequester Optional focus requester for programmatic focus.
 * @param colors Color configuration, defaults to [NumberFieldDefaults.colors].
 * @param textStyle Text style for input and prefix, defaults to [NumberFieldDefaults.textStyle].
 * @param dimens Dimension configuration, defaults to [NumberFieldDefaults.dimens].
 *
 * @see NumberFieldDefaults for default values
 * @since 0.0.1
 */
@Composable
fun NumberField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester? = null,
    colors: NumberFieldColors = NumberFieldDefaults.colors(),
    textStyle: TextStyle = NumberFieldDefaults.textStyle(),
    dimens: NumberFieldDimens = NumberFieldDefaults.dimens(),
) {
    var isFocused by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier,
        color = colors.containerColor,
        shape = dimens.shape,
        border = BorderStroke(
            width = dimens.borderWidth,
            color = if (isFocused) colors.focusedBorderColor else colors.unfocusedBorderColor,
        ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(IntrinsicSize.Min),
        ) {
            Text(
                text = stringResource(Res.string.auth_phone_country_code),
                color = colors.prefixColor,
                style = textStyle,
                modifier = Modifier.padding(16.dp),
            )

            VerticalDivider(
                thickness = dimens.dividerThickness,
                color = colors.dividerColor,
                modifier = Modifier.padding(vertical = 6.dp),
            )

            TextField(
                value = value,
                onValueChange = { newValue ->
                    val filtered = newValue.filter { it.isDigit() }.take(MAX_PHONE_DIGITS)
                    onValueChange(filtered)
                },
                textStyle = textStyle,
                singleLine = true,
                visualTransformation = PhoneVisualTransformation,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged { isFocused = it.isFocused }
                    .then(
                        if (focusRequester != null) Modifier.focusRequester(focusRequester)
                        else Modifier
                    ),
                placeholder = @Composable {
                    Text(
                        text = stringResource(Res.string.auth_phone_placeholder),
                        color = colors.placeholderColor,
                        style = textStyle,
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colors.containerColor,
                    unfocusedContainerColor = colors.containerColor,
                    disabledContainerColor = colors.containerColor,
                    errorContainerColor = colors.containerColor,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    focusedTextColor = colors.textColor,
                    unfocusedTextColor = colors.textColor,
                    disabledTextColor = colors.textColor,
                    errorTextColor = colors.textColor,
                    cursorColor = colors.cursorColor,
                    selectionColors = colors.selectionColors,
                ),
            )
        }
    }
}

private object PhoneVisualTransformation : VisualTransformation {

    // Format: (XX) XXX XX XX
    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text
        if (raw.isEmpty()) return TransformedText(text, OffsetMapping.Identity)

        val sb = StringBuilder()
        val origToTrans = IntArray(raw.length + 1)

        for (i in raw.indices) {
            when (i) {
                0 -> sb.append('(')
                2 -> sb.append(") ")
                5 -> sb.append(' ')
                7 -> sb.append(' ')
            }
            origToTrans[i] = sb.length
            sb.append(raw[i])
        }
        origToTrans[raw.length] = sb.length

        val formatted = sb.toString()
        val transToOrig = IntArray(formatted.length + 1)
        var oi = 0
        for (ti in 0..formatted.length) {
            while (oi <= raw.length && origToTrans[oi] < ti) oi++
            transToOrig[ti] = oi.coerceAtMost(raw.length)
        }

        return TransformedText(
            AnnotatedString(formatted),
            object : OffsetMapping {
                override fun originalToTransformed(offset: Int) =
                    origToTrans[offset.coerceIn(0, raw.length)]

                override fun transformedToOriginal(offset: Int) =
                    transToOrig[offset.coerceIn(0, formatted.length)]
            },
        )
    }
}
