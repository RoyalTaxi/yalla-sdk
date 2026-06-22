package uz.yalla.design.color

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ColorSchemeTest {
    @Test
    fun lightSchemeWiresEveryTokenToItsLightConstant() {
        val scheme = light()
        assertEquals(LightTextBase, scheme.text.base)
        assertEquals(LightTextWhite, scheme.text.white)
        assertEquals(LightBackgroundBase, scheme.background.base)
        assertEquals(LightBackgroundBrandBase, scheme.background.brand)
        assertEquals(LightButtonActive, scheme.button.active)
        assertEquals(LightIconBase, scheme.icon.base)
        assertEquals(LightBorderError, scheme.border.error)
    }

    @Test
    fun darkSchemeWiresEveryTokenToItsDarkConstant() {
        val scheme = dark()
        assertEquals(DarkTextBase, scheme.text.base)
        assertEquals(DarkBackgroundBase, scheme.background.base)
        assertEquals(DarkButtonDisabled, scheme.button.disabled)
        assertEquals(DarkIconBase, scheme.icon.base)
        assertTrue(dark().background.base != light().background.base)
    }

    @Test
    fun hoistedSchemesAreStableSingletons() {
        assertSame(LightColorScheme, LightColorScheme)
        assertSame(DarkColorScheme, DarkColorScheme)
        assertEquals(light().text.base, LightColorScheme.text.base)
        assertEquals(dark().text.base, DarkColorScheme.text.base)
    }

    @Test
    fun splashGradientSpansBoundsInsteadOfClampingAt1000px() {
        val expected =
            Brush.linearGradient(
                colors = listOf(Color(0xFF7957FF), Color(0xFF562DF8), Color(0xFF3812CE)),
                start = Offset.Zero,
                end = Offset.Infinite
            )
        assertEquals(expected, light().gradient.splash)
    }
}
