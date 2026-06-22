package uz.yalla.core.util

import kotlin.test.Test
import kotlin.test.assertEquals

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

    @Test
    fun resolvesPlaceholdersWithTenOrMoreArgs() {
        val template = "{0}-{1}-{2}-{3}-{4}-{5}-{6}-{7}-{8}-{9}-{10}-{11}"
        assertEquals(
            "a-b-c-d-e-f-g-h-i-j-k-l",
            template.formatArgs("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l")
        )
    }

    @Test
    fun aSubstitutedValueContainingAPlaceholderTokenIsRewrittenByLaterPasses() {
        assertEquals("raw X text", "{0}".formatArgs("raw {1} text", "X"))
    }
}
