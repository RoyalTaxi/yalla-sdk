package uz.yalla.core.util

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Characterization of [formatArgs] — the positional `{n}` placeholder substitution used for
 * interpolating localized templates. Pins the index semantics, repeated/unknown placeholders, and
 * non-string argument stringification.
 */
class StringFormattingTest {
    @Test
    fun replacesSinglePlaceholder() {
        assertEquals("Hello, Islom", "Hello, {0}".formatArgs("Islom"))
    }

    @Test
    fun replacesPlaceholdersByPosition() {
        assertEquals("3 + 4 = 7", "{0} + {1} = {2}".formatArgs(3, 4, 7))
    }

    @Test
    fun repeatedPlaceholderIsReplacedEveryOccurrence() {
        assertEquals("ab ab", "{0} {0}".formatArgs("ab"))
    }

    @Test
    fun unmatchedPlaceholderIsLeftIntact() {
        assertEquals("only {1} here", "only {1} here".formatArgs("first"))
    }

    @Test
    fun templateWithoutPlaceholdersIsUnchanged() {
        assertEquals("nothing to do", "nothing to do".formatArgs("ignored"))
    }

    @Test
    fun nonStringArgumentsAreStringified() {
        assertEquals("count: 42, on: true", "count: {0}, on: {1}".formatArgs(42, true))
    }
}
