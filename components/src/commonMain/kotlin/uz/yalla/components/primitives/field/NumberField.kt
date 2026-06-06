package uz.yalla.components.primitives.field

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme
import uz.yalla.resources.Res
import uz.yalla.resources.auth_phone_country_code
import uz.yalla.resources.auth_phone_placeholder

private const val MAX_PHONE_DIGITS = 9

@Composable
fun NumberField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Surface(
        modifier = modifier,
        color = System.color.background.base,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (isFocused) System.color.border.filled else System.color.border.disabled
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {
            Text(
                text = stringResource(Res.string.auth_phone_country_code),
                color = System.color.text.base,
                style = System.font.body.base.medium,
                modifier = Modifier.padding(16.dp)
            )

            VerticalDivider(
                thickness = 1.dp,
                color = System.color.border.disabled,
                modifier = Modifier.padding(vertical = 6.dp)
            )

            TextField(
                value = value,
                onValueChange = { onValueChange(it.filter(Char::isDigit).take(MAX_PHONE_DIGITS)) },
                textStyle = System.font.body.base.medium,
                singleLine = true,
                visualTransformation = PhoneVisualTransformation,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                interactionSource = interactionSource,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = stringResource(Res.string.auth_phone_placeholder),
                        color = System.color.text.subtle,
                        style = System.font.body.base.medium
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = System.color.background.base,
                    unfocusedContainerColor = System.color.background.base,
                    disabledContainerColor = System.color.background.base,
                    errorContainerColor = System.color.background.base,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    focusedTextColor = System.color.text.base,
                    unfocusedTextColor = System.color.text.base,
                    disabledTextColor = System.color.text.base,
                    errorTextColor = System.color.text.base,
                    cursorColor = System.color.text.link,
                    selectionColors = TextSelectionColors(
                        handleColor = System.color.text.link,
                        backgroundColor = System.color.text.link.copy(.3f)
                    )
                )
            )
        }
    }
}

private object PhoneVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text
        if (digits.isEmpty()) return TransformedText(text, OffsetMapping.Identity)

        val formatted = digits.format()

        return TransformedText(
            AnnotatedString(formatted),
            offsetMapping(
                digitsLength = digits.length,
                formattedText = formatted
            )
        )
    }

    private fun String.format() = buildString {
        this@format.forEachIndexed { index, digit ->
            when (index) {
                0 -> append('(')
                2 -> append(") ")
                5, 7 -> append(' ')
            }

            append(digit)
        }
    }

    private fun offsetMapping(
        digitsLength: Int,
        formattedText: String
    ) = object : OffsetMapping {
        override fun originalToTransformed(offset: Int) = offset.coerceIn(0, digitsLength).phoneOffset().coerceAtMost(formattedText.length)

        override fun transformedToOriginal(offset: Int) = formattedText.take(offset.coerceIn(0, formattedText.length)).count(Char::isDigit)
    }

    private fun Int.phoneOffset(): Int {
        var offset = this + 1
        if (this >= 2) offset += 2
        if (this >= 5) offset += 1
        if (this >= 7) offset += 1
        return offset
    }
}

@Preview(showSystemUi = true)
@Composable
private fun NumberFieldPreview() = YallaTheme {
    var value by remember { mutableStateOf("") }
    NumberField(
        value = value,
        onValueChange = { value = it },
        modifier = Modifier.padding(16.dp)
    )
}
