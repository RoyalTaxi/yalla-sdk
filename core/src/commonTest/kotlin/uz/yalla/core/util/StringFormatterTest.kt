package uz.yalla.core.util

import kotlin.test.Test
import kotlin.test.assertEquals

class StringFormatterTest {
    @Test
    fun shouldReplaceIndexedPlaceholdersWhenArgumentsProvided() {
        val template = "Hello, {0}! You have {1} messages."

        val formatted = template.formatArgs("Alice", 5)

        assertEquals("Hello, Alice! You have 5 messages.", formatted)
    }

    @Test
    fun shouldKeepPlaceholderWhenArgumentIsMissing() {
        val template = "Values: {0}, {1}, {2}"

        val formatted = template.formatArgs("A", "B")

        assertEquals("Values: A, B, {2}", formatted)
    }

    @Test
    fun shouldReplaceAllOccurrencesOfSamePlaceholder() {
        val template = "{0}-{0}-{1}"

        val formatted = template.formatArgs("x", "y")

        assertEquals("x-x-y", formatted)
    }
}
