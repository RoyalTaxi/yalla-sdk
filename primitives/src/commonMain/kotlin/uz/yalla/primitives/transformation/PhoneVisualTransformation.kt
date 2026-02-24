package uz.yalla.primitives.transformation

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Visual transformation for phone number formatting.
 *
 * Formats input according to a mask pattern while maintaining cursor position.
 *
 * ## Usage
 *
 * ```kotlin
 * TextField(
 *     value = phoneNumber,
 *     onValueChange = { phoneNumber = it },
 *     visualTransformation = PhoneVisualTransformation(
 *         mask = "+998 (##) ###-##-##",
 *         maskChar = '#'
 *     )
 * )
 * ```
 *
 * @param mask Format pattern. Use maskChar for input positions.
 * @param maskChar Character representing input positions in mask.
 */
class PhoneVisualTransformation(
    private val mask: String,
    private val maskChar: Char = '#',
) : VisualTransformation {
    private val maxLength = mask.count { it == maskChar }

    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = text.text.take(maxLength)
        val formatted = applyMask(trimmed)

        return TransformedText(
            text = AnnotatedString(formatted),
            offsetMapping = PhoneOffsetMapping(trimmed, formatted, mask, maskChar),
        )
    }

    private fun applyMask(text: String): String {
        if (text.isEmpty()) return ""

        val result = StringBuilder()
        var textIndex = 0

        for (maskIndex in mask.indices) {
            if (textIndex >= text.length) break

            val char = mask[maskIndex]
            if (char == maskChar) {
                result.append(text[textIndex])
                textIndex++
            } else {
                result.append(char)
            }
        }

        return result.toString()
    }
}

/**
 * Offset mapping for phone number transformation.
 */
private class PhoneOffsetMapping(
    private val original: String,
    private val formatted: String,
    private val mask: String,
    private val maskChar: Char,
) : OffsetMapping {
    override fun originalToTransformed(offset: Int): Int {
        var transformedOffset = 0
        var originalCount = 0

        for (i in mask.indices) {
            if (originalCount >= offset) break

            if (mask[i] == maskChar) {
                originalCount++
            }
            transformedOffset++
        }

        return transformedOffset.coerceAtMost(formatted.length)
    }

    override fun transformedToOriginal(offset: Int): Int {
        var originalOffset = 0

        for (i in 0 until offset.coerceAtMost(mask.length)) {
            if (mask[i] == maskChar) {
                originalOffset++
            }
        }

        return originalOffset.coerceAtMost(original.length)
    }
}
