package uz.yalla.components.primitive.transformation

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
 */
object MaskFormatter {
    /**
     * Formats text according to a mask pattern.
     *
     * @param text Raw text to format (typically digits only)
     * @param mask Pattern with placeholder characters
     * @param maskChar Character in mask that should be replaced by input
     * @return Formatted text with mask applied
     */
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

    /**
     * Counts the number of placeholder characters in a mask.
     *
     * @param mask Pattern to analyze
     * @param maskChar Placeholder character
     * @return Number of input positions available
     */
    fun countPlaceholders(
        mask: String,
        maskChar: Char = '_'
    ): Int = mask.count { it == maskChar }

    /**
     * Extracts raw input from formatted text.
     *
     * @param formatted Text with mask applied
     * @param mask Pattern used for formatting
     * @param maskChar Placeholder character
     * @return Raw input without mask characters
     */
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
