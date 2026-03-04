package uz.yalla.core.settings

import kotlin.test.Test
import kotlin.test.assertEquals

class ThemeKindTest {
    @Test
    fun shouldReturnThemeWhenIdMatches() {
        val theme = ThemeKind.from("dark")

        assertEquals(ThemeKind.Dark, theme)
    }

    @Test
    fun shouldNormalizeIdWhenFindingTheme() {
        val theme = ThemeKind.from("  LIGHT  ")

        assertEquals(ThemeKind.Light, theme)
    }

    @Test
    fun shouldReturnSystemWhenIdIsUnknownOrNull() {
        assertEquals(ThemeKind.System, ThemeKind.from("unknown"))
        assertEquals(ThemeKind.System, ThemeKind.from(null))
    }
}
