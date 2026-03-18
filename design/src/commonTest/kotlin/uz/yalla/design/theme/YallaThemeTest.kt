package uz.yalla.design.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import uz.yalla.design.color.ColorScheme
import uz.yalla.design.color.DarkTextBase
import uz.yalla.design.color.LightTextBase
import uz.yalla.design.font.FontScheme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@OptIn(ExperimentalTestApi::class)
class YallaThemeTest {

    @Test
    fun shouldProvideColorSchemeViaSystem() = runComposeUiTest {
        var capturedScheme: ColorScheme? = null
        setContent {
            YallaTheme(isDark = false) {
                capturedScheme = System.color
            }
        }
        assertNotNull(capturedScheme)
    }

    @Test
    fun shouldProvideFontSchemeViaSystem() = runComposeUiTest {
        var capturedFont: FontScheme? = null
        setContent {
            YallaTheme(isDark = false) {
                capturedFont = System.font
            }
        }
        assertNotNull(capturedFont)
    }

    @Test
    fun shouldUseLightSchemeWhenNotDark() = runComposeUiTest {
        var capturedTextBase: Color? = null
        setContent {
            YallaTheme(isDark = false) {
                capturedTextBase = System.color.text.base
            }
        }
        assertEquals(LightTextBase, capturedTextBase)
    }

    @Test
    fun shouldUseDarkSchemeWhenDark() = runComposeUiTest {
        var capturedTextBase: Color? = null
        setContent {
            YallaTheme(isDark = true) {
                capturedTextBase = System.color.text.base
            }
        }
        assertEquals(DarkTextBase, capturedTextBase)
    }

    @Test
    fun shouldProvideIsDarkFlag() = runComposeUiTest {
        var capturedIsDark: Boolean? = null
        setContent {
            YallaTheme(isDark = true) {
                capturedIsDark = System.isDark
            }
        }
        assertEquals(true, capturedIsDark)
    }
}
