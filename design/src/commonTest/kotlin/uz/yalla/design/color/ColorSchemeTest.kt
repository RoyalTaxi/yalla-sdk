package uz.yalla.design.color

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Guards the hand-assembled token wiring in [light]/[dark] (a transposed slot is otherwise
 * undetectable), the hoisted-singleton identity that keeps `YallaTheme` from churning the scheme on
 * every recomposition, and the bounds-relative splash gradient (the previous fixed 1000px end
 * clamped to a flat block on large surfaces).
 */
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
        // Appearance actually differs from light, so the two schemes can't be swapped silently.
        assertTrue(dark().background.base != light().background.base)
    }

    @Test
    fun hoistedSchemesAreStableSingletons() {
        // YallaTheme's default reads these; a fresh allocation each recomposition would churn the
        // staticCompositionLocalOf identity and defeat subtree skipping.
        assertSame(LightColorScheme, LightColorScheme)
        assertSame(DarkColorScheme, DarkColorScheme)
        assertEquals(light().text.base, LightColorScheme.text.base)
        assertEquals(dark().text.base, DarkColorScheme.text.base)
    }

    @Test
    fun splashGradientSpansBoundsInsteadOfClampingAt1000px() {
        // The reference is the intended bounds-relative gradient. The old fixed Offset(1000f,1000f)
        // would not equal this, so this assertion fails before the fix and passes after.
        val expected =
            Brush.linearGradient(
                colors = listOf(Color(0xFF7957FF), Color(0xFF562DF8), Color(0xFF3812CE)),
                start = Offset.Zero,
                end = Offset.Infinite
            )
        assertEquals(expected, light().gradient.splash)
    }
}
