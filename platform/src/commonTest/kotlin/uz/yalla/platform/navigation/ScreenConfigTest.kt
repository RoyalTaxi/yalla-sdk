package uz.yalla.platform.navigation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ScreenConfigTest {
    @Test
    fun shouldHaveSensibleDefaults() {
        val config = ScreenConfig()

        assertNull(config.title)
        assertEquals(LargeTitleMode.Never, config.largeTitleMode)
        assertTrue(config.showsNavigationBar)
        assertFalse(config.transparentNavigationBar)
    }

    @Test
    fun shouldSetAllProperties() {
        val config = ScreenConfig(
            title = "Menu",
            largeTitleMode = LargeTitleMode.Always,
            showsNavigationBar = true,
            transparentNavigationBar = true,
        )

        assertEquals("Menu", config.title)
        assertEquals(LargeTitleMode.Always, config.largeTitleMode)
        assertTrue(config.transparentNavigationBar)
    }

    @Test
    fun shouldSupportCopyWithModification() {
        val original = ScreenConfig(title = "A")
        val copy = original.copy(title = "B")

        assertEquals("A", original.title)
        assertEquals("B", copy.title)
    }

    @Test
    fun shouldHideTitleWhenNull() {
        val config = ScreenConfig(title = null, showsNavigationBar = true)
        assertNull(config.title)
        assertTrue(config.showsNavigationBar)
    }
}
