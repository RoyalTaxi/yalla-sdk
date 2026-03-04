package uz.yalla.core.util

import kotlin.test.Test
import kotlin.test.assertEquals

class NormalizationTest {
    @Test
    fun shouldReturnEmptyStringWhenIdIsNull() {
        val normalized = (null as String?).normalizedId()

        assertEquals("", normalized)
    }

    @Test
    fun shouldTrimAndLowercaseWhenIdHasWhitespaceAndUppercase() {
        val normalized = "  SoMe_Id  ".normalizedId()

        assertEquals("some_id", normalized)
    }

    @Test
    fun shouldReturnEmptyStringWhenLocaleCodeIsNull() {
        val normalized = (null as String?).normalizedLocaleCode()

        assertEquals("", normalized)
    }

    @Test
    fun shouldNormalizeLocaleCodeWhenValueContainsUnderscoreWhitespaceAndUppercase() {
        val normalized = "  UZ_CYRL  ".normalizedLocaleCode()

        assertEquals("uz-cyrl", normalized)
    }
}
