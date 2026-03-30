package uz.yalla.platform.navigation

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ScreenConfigTest {
    @Test
    fun shouldHaveSensibleDefaults() {
        val config = ScreenConfig()
        assertTrue(config.showsNavigationBar)
    }

    @Test
    fun shouldHideNavigationBar() {
        val config = ScreenConfig(showsNavigationBar = false)
        assertFalse(config.showsNavigationBar)
    }
}
