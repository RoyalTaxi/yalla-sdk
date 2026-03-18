package uz.yalla.design.color

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ColorSchemeTest {

    @Test
    fun shouldCreateLightSchemeWithCorrectTextColors() {
        val scheme = light()
        assertEquals(LightTextBase, scheme.text.base)
        assertEquals(LightTextSubtle, scheme.text.subtle)
        assertEquals(LightTextLink, scheme.text.link)
        assertEquals(LightTextRed, scheme.text.red)
        assertEquals(LightTextWhite, scheme.text.white)
    }

    @Test
    fun shouldCreateDarkSchemeWithCorrectTextColors() {
        val scheme = dark()
        assertEquals(DarkTextBase, scheme.text.base)
        assertEquals(DarkTextSubtle, scheme.text.subtle)
        assertEquals(DarkTextLink, scheme.text.link)
        assertEquals(DarkTextRed, scheme.text.red)
        assertEquals(DarkTextWhite, scheme.text.white)
    }

    @Test
    fun shouldCreateLightSchemeWithCorrectBackgroundColors() {
        val scheme = light()
        assertEquals(LightBackgroundBase, scheme.background.base)
        assertEquals(LightBackgroundBrandBase, scheme.background.brand)
        assertEquals(LightBackgroundSecondary, scheme.background.secondary)
        assertEquals(LightBackgroundTertiary, scheme.background.tertiary)
    }

    @Test
    fun shouldCreateDarkSchemeWithCorrectBackgroundColors() {
        val scheme = dark()
        assertEquals(DarkBackgroundBase, scheme.background.base)
        assertEquals(DarkBackgroundBrandBase, scheme.background.brand)
        assertEquals(DarkBackgroundSecondary, scheme.background.secondary)
        assertEquals(DarkBackgroundTertiary, scheme.background.tertiary)
    }

    @Test
    fun shouldCreateLightSchemeWithCorrectBorderColors() {
        val scheme = light()
        assertEquals(LightBorderDisabled, scheme.border.disabled)
        assertEquals(LightBorderFilled, scheme.border.filled)
        assertEquals(LightBorderWhite, scheme.border.white)
        assertEquals(LightBorderError, scheme.border.error)
    }

    @Test
    fun shouldCreateDarkSchemeWithCorrectBorderColors() {
        val scheme = dark()
        assertEquals(DarkBorderDisabled, scheme.border.disabled)
        assertEquals(DarkBorderFilled, scheme.border.filled)
        assertEquals(DarkBorderWhite, scheme.border.white)
        assertEquals(DarkBorderError, scheme.border.error)
    }

    @Test
    fun shouldCreateLightSchemeWithCorrectButtonColors() {
        val scheme = light()
        assertEquals(LightButtonActive, scheme.button.active)
        assertEquals(LightButtonDisabled, scheme.button.disabled)
        assertEquals(LightButtonSecondary, scheme.button.secondary)
        assertEquals(LightButtonTertiary, scheme.button.tertiary)
        assertEquals(LightButtonDisabledTertiary, scheme.button.disabledTertiary)
    }

    @Test
    fun shouldCreateDarkSchemeWithCorrectButtonColors() {
        val scheme = dark()
        assertEquals(DarkButtonActive, scheme.button.active)
        assertEquals(DarkButtonDisabled, scheme.button.disabled)
        assertEquals(DarkButtonSecondary, scheme.button.secondary)
        assertEquals(DarkButtonTertiary, scheme.button.tertiary)
        assertEquals(DarkButtonDisabledTertiary, scheme.button.disabledTertiary)
    }

    @Test
    fun shouldCreateLightSchemeWithCorrectIconColors() {
        val scheme = light()
        assertEquals(LightIconWhite, scheme.icon.white)
        assertEquals(LightIconBase, scheme.icon.base)
        assertEquals(LightIconSecondary, scheme.icon.secondary)
        assertEquals(LightIconDisabled, scheme.icon.disabled)
        assertEquals(LightIconRed, scheme.icon.red)
        assertEquals(LightIconSubtle, scheme.icon.subtle)
    }

    @Test
    fun shouldCreateDarkSchemeWithCorrectIconColors() {
        val scheme = dark()
        assertEquals(DarkIconWhite, scheme.icon.white)
        assertEquals(DarkIconBase, scheme.icon.base)
        assertEquals(DarkIconSecondary, scheme.icon.secondary)
        assertEquals(DarkIconDisabled, scheme.icon.disabled)
        assertEquals(DarkIconRed, scheme.icon.red)
        assertEquals(DarkIconSubtle, scheme.icon.subtle)
    }

    @Test
    fun shouldShareAccentColorsBetweenThemes() {
        val lightAccent = light().accent
        val darkAccent = dark().accent
        assertEquals(lightAccent.pinkSun, darkAccent.pinkSun)
        assertEquals(lightAccent.color1, darkAccent.color1)
        assertEquals(lightAccent.color2, darkAccent.color2)
        assertEquals(lightAccent.color3, darkAccent.color3)
        assertEquals(lightAccent.color4, darkAccent.color4)
        assertEquals(lightAccent.color5, darkAccent.color5)
    }

    @Test
    fun shouldShareGradientsBetweenThemes() {
        val lightGradient = light().gradient
        val darkGradient = dark().gradient
        assertEquals(lightGradient.splash, darkGradient.splash)
        assertEquals(lightGradient.sunsetNight, darkGradient.sunsetNight)
    }

    @Test
    fun shouldHaveDifferentTextColorsBetweenThemes() {
        assertNotEquals(light().text.base, dark().text.base)
    }

    @Test
    fun shouldCopyWithModifiedTextColors() {
        val original = light()
        val modified = original.copy(
            text = original.text.copy(base = DarkTextBase),
        )
        assertEquals(DarkTextBase, modified.text.base)
        assertEquals(original.text.subtle, modified.text.subtle)
        assertEquals(original.background, modified.background)
    }
}
