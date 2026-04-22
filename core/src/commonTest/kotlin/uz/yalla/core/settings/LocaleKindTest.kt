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
    fun shouldReturnLocaleWhenCodeHasDifferentCase() {
        val locale = LocaleKind.from("UZ")

        assertEquals(LocaleKind.Uz, locale)
    }

    @Test
    fun shouldFallBackToUzWhenRemovedCodePersistedFromBeforeAdr014() {
        // ADR-014 dropped "en" and "uz-Cyrl". Persisted values fall through to Uz.
        assertEquals(LocaleKind.Uz, LocaleKind.from("en"))
        assertEquals(LocaleKind.Uz, LocaleKind.from("uz-Cyrl"))
    }

    @Test
    fun shouldReturnUzWhenCodeIsUnknownOrNull() {
        assertEquals(LocaleKind.Uz, LocaleKind.from("unknown"))
        assertEquals(LocaleKind.Uz, LocaleKind.from(null))
    }
}
