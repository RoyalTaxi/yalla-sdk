package uz.yalla.core.util

import kotlin.test.Test
import kotlin.test.assertEquals

class MaskFormatterTest {
    @Test
    fun emptyTextReturnsEmptyRegardlessOfMask() {
        assertEquals("", MaskFormatter.format("", "+___"))
        assertEquals("", MaskFormatter.format("", ""))
        assertEquals("", MaskFormatter.format("", "(___) ___-____"))
    }

    @Test
    fun fillsUnderscoresWithSourceCharactersInOrder() {
        assertEquals("12-34", MaskFormatter.format("1234", "__-__"))
        assertEquals("(123) 456-7890", MaskFormatter.format("1234567890", "(___) ___-____"))
    }

    @Test
    fun rendersFullUzbekPhoneMask() {
        assertEquals("+998 90 123 45 67", MaskFormatter.format("998901234567", "+___ __ ___ __ __"))
    }

    @Test
    fun stopsAtPartialInputAfterEmittingPrecedingLiterals() {
        assertEquals("+1", MaskFormatter.format("1", "+___"))
        assertEquals("+998 90", MaskFormatter.format("99890", "+___ __ ___ __ __"))
    }

    @Test
    fun trailingLiteralIsNotEmittedOnceSourceIsExhausted() {
        assertEquals("12", MaskFormatter.format("12", "__-__"))
    }

    @Test
    fun separatorLiteralAppearsOnlyWhenMoreTextFollows() {
        assertEquals("12-3", MaskFormatter.format("123", "__-__"))
    }

    @Test
    fun maskShorterThanTextTruncatesToMaskFillSlots() {
        assertEquals("a", MaskFormatter.format("ab", "_"))
        assertEquals("12", MaskFormatter.format("123456", "__"))
    }

    @Test
    fun maskWithoutFillTokensEmitsLiteralsWhileTextRemains() {
        assertEquals("xyz", MaskFormatter.format("abc", "xyz"))
        assertEquals("()", MaskFormatter.format("1", "()"))
    }

    @Test
    fun emptyMaskWithNonEmptyTextReturnsEmpty() {
        assertEquals("", MaskFormatter.format("1234", ""))
    }

    @Test
    fun singleUnderscoreTakesOnlyTheFirstCharacter() {
        assertEquals("5", MaskFormatter.format("5", "_____"))
        assertEquals("5", MaskFormatter.format("5", "_"))
    }
}
