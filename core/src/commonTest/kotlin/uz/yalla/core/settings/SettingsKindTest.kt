package uz.yalla.core.settings

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Characterization of the settings enum decoders: [MapKind.from], [ThemeKind.from], and
 * [LocaleKind.from].
 *
 * Each decoder normalizes (trim + lowercase) and falls back to a specific default on any
 * unrecognized or null input. The defaults are the load-bearing contract — a careless edit that
 * changes the fallback flips the app's map provider, theme, or language. [LocaleKind] additionally
 * canonicalizes `_` to `-` before matching, which is pinned separately.
 */
class SettingsKindTest {
    // --- MapKind: default Google ---

    @Test
    fun mapKind_decodesKnownIds() {
        assertEquals(MapKind.Google, MapKind.from("google"))
        assertEquals(MapKind.Libre, MapKind.from("libre"))
    }

    @Test
    fun mapKind_normalizesCaseAndWhitespace() {
        assertEquals(MapKind.Libre, MapKind.from("  LIBRE  "))
        assertEquals(MapKind.Google, MapKind.from("Google"))
    }

    @Test
    fun mapKind_fallsBackToGoogleForUnknownAndNull() {
        assertEquals(MapKind.Google, MapKind.from(null))
        assertEquals(MapKind.Google, MapKind.from(""))
        assertEquals(MapKind.Google, MapKind.from("osm"))
    }

    @Test
    fun mapKind_idsAreStable() {
        assertEquals("google", MapKind.Google.id)
        assertEquals("libre", MapKind.Libre.id)
    }

    // --- ThemeKind: default System ---

    @Test
    fun themeKind_decodesKnownIds() {
        assertEquals(ThemeKind.Light, ThemeKind.from("light"))
        assertEquals(ThemeKind.Dark, ThemeKind.from("dark"))
        assertEquals(ThemeKind.System, ThemeKind.from("system"))
    }

    @Test
    fun themeKind_normalizesCaseAndWhitespace() {
        assertEquals(ThemeKind.Dark, ThemeKind.from(" DARK "))
        assertEquals(ThemeKind.Light, ThemeKind.from("Light"))
    }

    @Test
    fun themeKind_fallsBackToSystemForUnknownAndNull() {
        assertEquals(ThemeKind.System, ThemeKind.from(null))
        assertEquals(ThemeKind.System, ThemeKind.from(""))
        assertEquals(ThemeKind.System, ThemeKind.from("amoled"))
    }

    @Test
    fun themeKind_idsAreStable() {
        assertEquals("light", ThemeKind.Light.id)
        assertEquals("dark", ThemeKind.Dark.id)
        assertEquals("system", ThemeKind.System.id)
    }

    // --- LocaleKind: default Uz, with '_' -> '-' canonicalization ---

    @Test
    fun localeKind_decodesKnownCodes() {
        assertEquals(LocaleKind.Uz, LocaleKind.from("uz"))
        assertEquals(LocaleKind.Ru, LocaleKind.from("ru"))
    }

    @Test
    fun localeKind_normalizesCaseAndWhitespace() {
        assertEquals(LocaleKind.Ru, LocaleKind.from("  RU  "))
        assertEquals(LocaleKind.Uz, LocaleKind.from("Uz"))
    }

    @Test
    fun localeKind_canonicalizesUnderscoreToHyphenBeforeMatching() {
        // The codes are bare ("uz"/"ru"), so a region tag never matches even after canonicalization;
        // what is pinned here is that the underscore is rewritten so future region-aware codes share
        // one normalized form. Both still fall back to Uz today.
        assertEquals(LocaleKind.Uz, LocaleKind.from("uz_UZ"))
        assertEquals(LocaleKind.Uz, LocaleKind.from("ru_RU"))
        assertEquals(LocaleKind.from("uz-UZ"), LocaleKind.from("uz_UZ"))
    }

    @Test
    fun localeKind_fallsBackToUzForUnknownAndNull() {
        assertEquals(LocaleKind.Uz, LocaleKind.from(null))
        assertEquals(LocaleKind.Uz, LocaleKind.from(""))
        assertEquals(LocaleKind.Uz, LocaleKind.from("en"))
    }

    @Test
    fun localeKind_codeAndDisplayNameAreStable() {
        assertEquals("uz", LocaleKind.Uz.code)
        assertEquals("O'zbekcha", LocaleKind.Uz.displayName)
        assertEquals("ru", LocaleKind.Ru.code)
        assertEquals("Русский", LocaleKind.Ru.displayName)
    }
}
