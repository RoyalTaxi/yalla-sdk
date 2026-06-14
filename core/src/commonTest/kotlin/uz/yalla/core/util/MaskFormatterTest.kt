package uz.yalla.core.util

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Characterization of [MaskFormatter.format].
 *
 * `_` is the fill token (consumes one source char); every other mask char is a literal.
 * The non-obvious contract pinned here: the formatter STOPS as soon as the source runs out,
 * so a trailing literal is emitted only when there is still text to place after it (e.g.
 * "12" against "__-__" yields "12", not "12-"). Mirrors how phone/card masks render partial
 * input, so a regression silently corrupts every masked field.
 */
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
        // Leading literal '+' is emitted (text remains), then the single available digit fills,
        // then the formatter stops before the next fill slot.
        assertEquals("+1", MaskFormatter.format("1", "+___"))
        assertEquals("+998 90", MaskFormatter.format("99890", "+___ __ ___ __ __"))
    }

    @Test
    fun trailingLiteralIsNotEmittedOnceSourceIsExhausted() {
        // Both digits consumed -> index hits the end -> the '-' separator is never reached.
        assertEquals("12", MaskFormatter.format("12", "__-__"))
    }

    @Test
    fun separatorLiteralAppearsOnlyWhenMoreTextFollows() {
        // One extra char past the separator is enough to surface it.
        assertEquals("12-3", MaskFormatter.format("123", "__-__"))
    }

    @Test
    fun maskShorterThanTextTruncatesToMaskFillSlots() {
        assertEquals("a", MaskFormatter.format("ab", "_"))
        assertEquals("12", MaskFormatter.format("123456", "__"))
    }

    @Test
    fun maskWithoutFillTokensEmitsLiteralsWhileTextRemains() {
        // No '_' means index never advances; literals are emitted for the whole mask
        // because there is still (unconsumed) text on every iteration.
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
