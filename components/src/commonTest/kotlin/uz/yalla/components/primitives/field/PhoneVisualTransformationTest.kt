package uz.yalla.components.primitives.field

import kotlin.test.Test
import kotlin.test.assertEquals

class PhoneVisualTransformationTest {
    @Test
    fun formatsFullNationalNumber() {
        assertEquals("(91) 234 56 78", formatPhoneDigits("912345678"))
    }

    @Test
    fun formatsAtEachSeparatorBoundary() {
        assertEquals("(9", formatPhoneDigits("9"))
        assertEquals("(91", formatPhoneDigits("91"))
        assertEquals("(91) 2", formatPhoneDigits("912"))
        assertEquals("(91) 234 5", formatPhoneDigits("912345"))
        assertEquals("(91) 234 56 7", formatPhoneDigits("91234567"))
    }

    @Test
    fun originalToTransformedAtKnownPositions() {
        val digits = "912345678"
        val formatted = formatPhoneDigits(digits)
        assertEquals(0, phoneOriginalToTransformed(0, digits.length, formatted.length))
        assertEquals(2, phoneOriginalToTransformed(1, digits.length, formatted.length))
        assertEquals(5, phoneOriginalToTransformed(2, digits.length, formatted.length))
        assertEquals(formatted.length, phoneOriginalToTransformed(9, digits.length, formatted.length))
    }

    @Test
    fun roundTripIsLosslessForEveryCursorPosition() {
        val digits = "912345678"
        val formatted = formatPhoneDigits(digits)
        for (n in 0..digits.length) {
            val transformed = phoneOriginalToTransformed(n, digits.length, formatted.length)
            assertEquals(
                n,
                phoneTransformedToOriginal(transformed, formatted),
                "round-trip failed at original offset $n"
            )
        }
    }

    @Test
    fun outOfRangeCursorIsClamped() {
        val digits = "912"
        val formatted = formatPhoneDigits(digits)
        assertEquals(formatted.length, phoneOriginalToTransformed(99, digits.length, formatted.length))
        assertEquals(0, phoneOriginalToTransformed(-5, digits.length, formatted.length))
        assertEquals(digits.length, phoneTransformedToOriginal(99, formatted))
    }
}
