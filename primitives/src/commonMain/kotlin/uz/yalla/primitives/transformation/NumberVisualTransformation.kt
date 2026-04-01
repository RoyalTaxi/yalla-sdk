package uz.yalla.primitives.transformation

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Visual transformation for generic number formatting with mask.
 *
 * Applies a mask pattern to numeric input, replacing mask characters with input digits.
 * Uses [MaskFormatter] for the underlying formatting logic. Similar to
 * [PhoneVisualTransformation] but for general numeric input such as card numbers
 * or document IDs.
 *
 * ## Usage
 *
 * ```kotlin
 * TextField(
 *     value = cardNumber,
 *     onValueChange = { cardNumber = it },
 *     visualTransformation = NumberVisualTransformation(
 *         mask = "____-____-____-____",
 *         maskChar = '_'
 *     )
 * )
 * ```
 *
 * @param mask Format pattern where [maskChar] positions are replaced by input characters.
 *   All other characters are inserted as literal separators.
 * @param maskChar Character in the mask that represents an input position. Defaults to `_`.
 *
 * @see PhoneVisualTransformation for phone-specific formatting
 * @see MaskFormatter for the underlying formatting utility
 * @since 0.0.1
 */
class NumberVisualTransformation(
    private val mask: String,
    private val maskChar: Char = '_',
) : VisualTransformation {
    private val maxLength = mask.count { it == maskChar }

    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = text.text.take(maxLength)
        val formatted = MaskFormatter.format(trimmed, mask, maskChar)

        return TransformedText(
            text = AnnotatedString(formatted),
            offsetMapping = NumberOffsetMapping(trimmed, formatted, mask, maskChar),
        )
    }
}

/**
 * Offset mapping for number transformation.
 */
private class NumberOffsetMapping(
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
