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
 * strips the region/script subtag, so a region-tagged code resolves by its primary language subtag;
 * that is pinned separately.
 */
class SettingsKindTest {
    // --- MapKind: default Google ---

    @Test
    fun mapKindDecodesKnownIds() {
        assertEquals(MapKind.Google, MapKind.from("google"))
        assertEquals(MapKind.Libre, MapKind.from("libre"))
    }

    @Test
    fun mapKindNormalizesCaseAndWhitespace() {
        assertEquals(MapKind.Libre, MapKind.from("  LIBRE  "))
        assertEquals(MapKind.Google, MapKind.from("Google"))
    }

    @Test
    fun mapKindFallsBackToGoogleForUnknownAndNull() {
        assertEquals(MapKind.Google, MapKind.from(null))
        assertEquals(MapKind.Google, MapKind.from(""))
        assertEquals(MapKind.Google, MapKind.from("osm"))
    }

    @Test
    fun mapKindIdsAreStable() {
        assertEquals("google", MapKind.Google.id)
        assertEquals("libre", MapKind.Libre.id)
    }

    // --- ThemeKind: default System ---

    @Test
    fun themeKindDecodesKnownIds() {
        assertEquals(ThemeKind.Light, ThemeKind.from("light"))
        assertEquals(ThemeKind.Dark, ThemeKind.from("dark"))
        assertEquals(ThemeKind.System, ThemeKind.from("system"))
    }

    @Test
    fun themeKindNormalizesCaseAndWhitespace() {
        assertEquals(ThemeKind.Dark, ThemeKind.from(" DARK "))
        assertEquals(ThemeKind.Light, ThemeKind.from("Light"))
    }

    @Test
    fun themeKindFallsBackToSystemForUnknownAndNull() {
        assertEquals(ThemeKind.System, ThemeKind.from(null))
        assertEquals(ThemeKind.System, ThemeKind.from(""))
        assertEquals(ThemeKind.System, ThemeKind.from("amoled"))
    }

    @Test
    fun themeKindIdsAreStable() {
        assertEquals("light", ThemeKind.Light.id)
        assertEquals("dark", ThemeKind.Dark.id)
        assertEquals("system", ThemeKind.System.id)
    }

    // --- LocaleKind: default Uz, region/script subtag stripped before matching ---

    @Test
    fun localeKindDecodesKnownCodes() {
        assertEquals(LocaleKind.Uz, LocaleKind.from("uz"))
        assertEquals(LocaleKind.Ru, LocaleKind.from("ru"))
    }

    @Test
    fun localeKindNormalizesCaseAndWhitespace() {
        assertEquals(LocaleKind.Ru, LocaleKind.from("  RU  "))
        assertEquals(LocaleKind.Uz, LocaleKind.from("Uz"))
    }

    @Test
    fun localeKindResolvesRegionTaggedCodesByPrimaryLanguageSubtag() {
        // The region/script subtag is stripped before matching, so a platform locale string maps
        // to its language — "ru-RU"/"ru_RU" is Russian (not a silent Uz fallback).
        assertEquals(LocaleKind.Uz, LocaleKind.from("uz_UZ"))
        assertEquals(LocaleKind.Ru, LocaleKind.from("ru_RU"))
        assertEquals(LocaleKind.Ru, LocaleKind.from("ru-RU"))
        assertEquals(LocaleKind.Uz, LocaleKind.from("uz-Latn-UZ"))
        assertEquals(LocaleKind.from("uz-UZ"), LocaleKind.from("uz_UZ"))
    }

    @Test
    fun localeKindFallsBackToUzForUnknownAndNull() {
        assertEquals(LocaleKind.Uz, LocaleKind.from(null))
        assertEquals(LocaleKind.Uz, LocaleKind.from(""))
        assertEquals(LocaleKind.Uz, LocaleKind.from("en"))
    }

    @Test
    fun localeKindCodeAndDisplayNameAreStable() {
        assertEquals("uz", LocaleKind.Uz.code)
        assertEquals("O'zbekcha", LocaleKind.Uz.displayName)
        assertEquals("ru", LocaleKind.Ru.code)
        assertEquals("Русский", LocaleKind.Ru.displayName)
    }
}
