package uz.yalla.core.util

import kotlin.test.Test
import kotlin.test.assertEquals

class NormalizationTest {
    @Test
    fun nullBecomesEmpty() {
        assertEquals("", null.normalizedId())
    }

    @Test
    fun blankAndWhitespaceBecomeEmpty() {
        assertEquals("", "   ".normalizedId())
        assertEquals("", "\t\n".normalizedId())
    }

    @Test
    fun trimsSurroundingWhitespaceAndLowercases() {
        assertEquals("abc", "  ABC \n".normalizedId())
        assertEquals("in_progress", "  IN_PROGRESS  ".normalizedId())
    }

    @Test
    fun alreadyNormalizedPassesThroughUnchanged() {
        assertEquals("start", "start".normalizedId())
    }
}
