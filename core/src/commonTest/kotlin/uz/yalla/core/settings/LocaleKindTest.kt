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
    fun shouldNormalizeUnderscoreToHyphenInLocaleCode() {
        // Underscore-separated locale codes (e.g. Java Locale toString output)
        // are normalized to hyphens before matching, so they fall into the
        // unknown-locale bucket and resolve to Uz.
        assertEquals(LocaleKind.Uz, LocaleKind.from("uz_Cyrl"))
        assertEquals(LocaleKind.Uz, LocaleKind.from("  RU_RU  "))
    }

    @Test
    fun shouldReturnUzWhenCodeIsUnknownOrNull() {
        assertEquals(LocaleKind.Uz, LocaleKind.from("unknown"))
        assertEquals(LocaleKind.Uz, LocaleKind.from(null))
    }
}
