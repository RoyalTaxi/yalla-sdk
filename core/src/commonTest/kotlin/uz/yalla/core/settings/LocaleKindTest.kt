package uz.yalla.core.settings

import kotlin.test.Test
import kotlin.test.assertEquals

class LocaleKindTest {
    @Test
    fun shouldReturnLocaleWhenCodeMatches() {
        val locale = LocaleKind.from("ru")

        assertEquals(LocaleKind.Ru, locale)
    }

    @Test
    fun shouldNormalizeLocaleCodeWhenContainsUppercaseAndUnderscore() {
        val locale = LocaleKind.from("  UZ_CYRL  ")

        assertEquals(LocaleKind.UzCyrillic, locale)
    }

    @Test
    fun shouldReturnLocaleWhenCodeHasDifferentCase() {
        val locale = LocaleKind.from("EN")

        assertEquals(LocaleKind.En, locale)
    }

    @Test
    fun shouldReturnUzWhenCodeIsUnknownOrNull() {
        assertEquals(LocaleKind.Uz, LocaleKind.from("unknown"))
        assertEquals(LocaleKind.Uz, LocaleKind.from(null))
    }
}
