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

    @Test
    fun resolvesPlaceholdersWithTenOrMoreArgs() {
        // {1} is substituted before {10}/{11}, but the literal token is "{1}" (with braces), so the
        // "{1"-prefix of "{10}" is never matched — each index resolves to its own arg.
        val template = "{0}-{1}-{2}-{3}-{4}-{5}-{6}-{7}-{8}-{9}-{10}-{11}"
        assertEquals(
            "a-b-c-d-e-f-g-h-i-j-k-l",
            template.formatArgs("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l")
        )
    }

    @Test
    fun aSubstitutedValueContainingAPlaceholderTokenIsRewrittenByLaterPasses() {
        // Pinned wart: substitution is sequential, so a value injected by {0} that itself contains
        // "{1}" IS clobbered by the {1} pass. Documents the order-of-substitution behavior.
        assertEquals("raw X text", "{0}".formatArgs("raw {1} text", "X"))
    }
}
