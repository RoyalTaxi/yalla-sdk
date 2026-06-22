package uz.yalla.core.settings

import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsKindTest {

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
