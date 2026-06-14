package uz.yalla.components.primitives.field

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme
import uz.yalla.foundation.theme.rememberIsDarkTheme
import uz.yalla.resources.icons.Scan
import uz.yalla.resources.icons.YallaIcons

public enum class CardFieldMask {
    CARD_NUMBER,
    EXPIRY
}

public class CardFieldController(
    value: String = "",
    placeholder: String = "",
    mask: CardFieldMask = CardFieldMask.CARD_NUMBER,
    isError: Boolean = false,
    showScanIcon: Boolean = false,
    onValueChange: (String) -> Unit
) {
    private var valueState by mutableStateOf(value)
    private var errorState by mutableStateOf(isError)
    private val transformation =
        if (mask ==
            CardFieldMask.CARD_NUMBER
        ) {
            CardNumberVisualTransformation
        } else {
            ExpiryDateVisualTransformation
        }
    private val maxLength = if (mask == CardFieldMask.CARD_NUMBER) 16 else 4

    @OptIn(ExperimentalComposeUiApi::class)
    public val viewController: UIViewController =
        ComposeUIViewController(configure = { opaque = false }) {
            YallaTheme(isDark = rememberIsDarkTheme()) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = valueState,
                        onValueChange = { raw ->
                            val filtered = raw.filter(Char::isDigit).take(maxLength)
                            if (filtered != valueState) {
                                valueState = filtered
                                onValueChange(filtered)
                            }
                        },
                        textStyle = System.font.body.base.medium,
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        visualTransformation = transformation,
                        trailingIcon =
                            if (showScanIcon) {
                                {
                                    Icon(
                                        painter = rememberVectorPainter(YallaIcons.Scan),
                                        contentDescription = null,
                                        tint = System.color.icon.base
                                    )
                                }
                            } else {
                                null
                            },
                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                        placeholder = {
                            Text(
                                text = placeholder,
                                style = System.font.body.base.medium,
                                color = System.color.text.subtle
                            )
                        },
                        colors =
                            OutlinedTextFieldDefaults.colors(
                                focusedTextColor = if (errorState) System.color.text.red else System.color.text.base,
                                unfocusedTextColor = if (errorState) System.color.text.red else System.color.text.base,
                                focusedBorderColor = System.color.border.filled,
                                unfocusedBorderColor = System.color.border.disabled,
                                cursorColor = System.color.text.link,
                                selectionColors =
                                    TextSelectionColors(
                                        handleColor = System.color.text.link,
                                        backgroundColor =
                                            System.color.text.link
                                                .copy(.3f)
                                    )
                            ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

    public fun setValue(value: String) {
        valueState = value
    }

    public fun setError(isError: Boolean) {
        errorState = isError
    }
}

private const val CARD_MASK = "#### #### #### ####"
private const val EXPIRY_MASK = "##/##"
private const val MASK_CHAR = '#'

private object CardNumberVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = text.text.take(16)
        val formatted = applyMask(trimmed, CARD_MASK)
        return TransformedText(
            AnnotatedString(formatted),
            MaskOffsetMapping(trimmed, formatted, CARD_MASK)
        )
    }
}

private object ExpiryDateVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = text.text.take(4)
        val formatted = applyMask(trimmed, EXPIRY_MASK)
        return TransformedText(
            AnnotatedString(formatted),
            MaskOffsetMapping(trimmed, formatted, EXPIRY_MASK)
        )
    }
}

private fun applyMask(
    text: String,
    mask: String
): String {
    if (text.isEmpty()) return ""
    val result = StringBuilder()
    var textIndex = 0
    for (maskIndex in mask.indices) {
        if (textIndex >= text.length) break
        if (mask[maskIndex] == MASK_CHAR) {
            result.append(text[textIndex])
            textIndex++
        } else {
            result.append(mask[maskIndex])
        }
    }
    return result.toString()
}

private class MaskOffsetMapping(
    private val original: String,
    private val formatted: String,
    private val mask: String
) : OffsetMapping {
    override fun originalToTransformed(offset: Int): Int {
        var transformedOffset = 0
        var originalCount = 0
        for (i in mask.indices) {
            if (originalCount >= offset) break
            if (mask[i] == MASK_CHAR) originalCount++
            transformedOffset++
        }
        return transformedOffset.coerceAtMost(formatted.length)
    }

    override fun transformedToOriginal(offset: Int): Int {
        var originalOffset = 0
        for (i in 0 until offset.coerceAtMost(mask.length)) {
            if (mask[i] == MASK_CHAR) originalOffset++
        }
        return originalOffset.coerceAtMost(original.length)
    }
}
