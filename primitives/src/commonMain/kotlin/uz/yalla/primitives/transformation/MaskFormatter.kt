package uz.yalla.primitives.transformation

/**
 * Utility for formatting text according to a mask pattern.
 *
 * ## Usage
 *
 * ```kotlin
 * val formatted = MaskFormatter.format(
 *     text = "998901234567",
 *     mask = "+### (##) ###-##-##",
 *     maskChar = '#'
 * )
 * // Result: "+998 (90) 123-45-67"
 * ```
 *
 * @see PhoneVisualTransformation for phone number formatting
 * @see NumberVisualTransformation for generic number formatting
 */
object MaskFormatter {
    /** Formats [text] according to [mask], replacing [maskChar] positions with input characters. */
    fun format(
        text: String,
        mask: String,
        maskChar: Char = '_',
    ): String {
        if (text.isEmpty()) return ""

        val result = StringBuilder()
        var textIndex = 0

        for (maskIndex in mask.indices) {
            if (textIndex >= text.length) break

            val maskCharacter = mask[maskIndex]
            if (maskCharacter == maskChar) {
                result.append(text[textIndex])
                textIndex++
            } else {
                result.append(maskCharacter)
            }
        }

        return result.toString()
    }

    /** Counts the number of [maskChar] positions in [mask]. */
    fun countPlaceholders(
        mask: String,
        maskChar: Char = '_'
    ): Int = mask.count { it == maskChar }

    /** Extracts raw input from [formatted] text by keeping only [maskChar] positions. */
    fun extractRaw(
        formatted: String,
        mask: String,
        maskChar: Char = '_',
    ): String {
        val result = StringBuilder()

        for (i in formatted.indices) {
            if (i < mask.length && mask[i] == maskChar) {
                result.append(formatted[i])
            }
        }

        return result.toString()
    }
}
