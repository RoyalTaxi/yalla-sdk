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
}
