package uz.yalla.core.util

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Pins [normalizedId] — the shared trim+lowercase normalization behind every `from(id)` wire
 * decoder. A regression here (dropped trim, locale-sensitive lowercase) would otherwise surface as
 * scattered decoder failures with no single owning test.
 */
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
