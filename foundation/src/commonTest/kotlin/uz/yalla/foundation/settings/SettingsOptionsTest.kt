package uz.yalla.foundation.settings

import uz.yalla.core.settings.LocaleKind
import uz.yalla.core.settings.MapKind
import uz.yalla.core.settings.ThemeKind
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SettingsOptionsTest {

    // --- ThemeOption ---

    @Test
    fun shouldMapThemeKindLightToThemeOptionLight() {
        assertEquals(ThemeOption.Light, ThemeOption.from(ThemeKind.Light))
    }

    @Test
    fun shouldMapThemeKindDarkToThemeOptionDark() {
        assertEquals(ThemeOption.Dark, ThemeOption.from(ThemeKind.Dark))
    }

    @Test
    fun shouldMapThemeKindSystemToThemeOptionSystem() {
        assertEquals(ThemeOption.System, ThemeOption.from(ThemeKind.System))
    }

    @Test
    fun shouldContainAllThemeOptions() {
        val all = ThemeOption.all
        assertEquals(3, all.size)
        assertTrue(all.contains(ThemeOption.Light))
        assertTrue(all.contains(ThemeOption.Dark))
        assertTrue(all.contains(ThemeOption.System))
    }

    @Test
    fun shouldRoundTripThemeKind() {
        ThemeOption.all.forEach { option ->
            assertEquals(option, ThemeOption.from(option.kind))
        }
    }

    // --- LanguageOption ---

    @Test
    fun shouldMapLocaleKindUzToLanguageOptionUzbek() {
        assertEquals(LanguageOption.Uzbek, LanguageOption.from(LocaleKind.Uz))
    }

    @Test
    fun shouldMapLocaleKindRuToLanguageOptionRussian() {
        assertEquals(LanguageOption.Russian, LanguageOption.from(LocaleKind.Ru))
    }

    @Test
    fun shouldContainAllLanguageOptions() {
        val all = LanguageOption.all
        assertEquals(2, all.size)
        assertTrue(all.contains(LanguageOption.Uzbek))
        assertTrue(all.contains(LanguageOption.Russian))
    }

    @Test
    fun shouldRoundTripLocaleKind() {
        LanguageOption.all.forEach { option ->
            assertEquals(option, LanguageOption.from(option.kind))
        }
    }

    // --- MapOption ---

    @Test
    fun shouldMapMapKindGoogleToMapOptionGoogle() {
        assertEquals(MapOption.Google, MapOption.from(MapKind.Google))
    }

    @Test
    fun shouldMapMapKindLibreToMapOptionLibre() {
        assertEquals(MapOption.Libre, MapOption.from(MapKind.Libre))
    }

    @Test
    fun shouldContainAllMapOptions() {
        val all = MapOption.all
        assertEquals(2, all.size)
        assertTrue(all.contains(MapOption.Google))
        assertTrue(all.contains(MapOption.Libre))
    }

    @Test
    fun shouldRoundTripMapKind() {
        MapOption.all.forEach { option ->
            assertEquals(option, MapOption.from(option.kind))
        }
    }
}
