package uz.yalla.primitives.transformation

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Visual transformation for generic number formatting with mask.
 *
 * Similar to [PhoneVisualTransformation] but for general numeric input.
 *
 * @param mask Format pattern
 * @param maskChar Character representing input positions
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
