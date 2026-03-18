package uz.yalla.firebase.analytics

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AnalyticsEventTest {

    // --- ScreenView ---

    @Test
    fun shouldCreateScreenViewWithCorrectName() {
        val event = AnalyticsEvent.ScreenView("Home")
        assertEquals("screen_view", event.name)
    }

    @Test
    fun shouldCreateScreenViewWithScreenNameParam() {
        val event = AnalyticsEvent.ScreenView("Home")
        assertEquals("Home", event.params?.get("screen_name"))
    }

    @Test
    fun shouldCreateScreenViewWithOptionalScreenClass() {
        val event = AnalyticsEvent.ScreenView("Home", "HomeActivity")
        assertEquals("HomeActivity", event.params?.get("screen_class"))
    }

    @Test
    fun shouldExcludeScreenClassWhenNull() {
        val event = AnalyticsEvent.ScreenView("Home")
        assertFalse(event.params?.containsKey("screen_class") == true)
    }

    // --- ButtonClick ---

    @Test
    fun shouldCreateButtonClickWithCorrectName() {
        val event = AnalyticsEvent.ButtonClick("login")
        assertEquals("button_click", event.name)
    }

    @Test
    fun shouldCreateButtonClickWithButtonNameParam() {
        val event = AnalyticsEvent.ButtonClick("login")
        assertEquals("login", event.params?.get("button_name"))
    }

    @Test
    fun shouldCreateButtonClickWithOptionalSource() {
        val event = AnalyticsEvent.ButtonClick("login", "header")
        assertEquals("header", event.params?.get("source"))
    }

    @Test
    fun shouldExcludeSourceWhenNull() {
        val event = AnalyticsEvent.ButtonClick("login")
        assertFalse(event.params?.containsKey("source") == true)
    }

    // --- Custom ---

    @Test
    fun shouldCreateCustomEventWithNameAndParams() {
        val event = AnalyticsEvent.Custom("purchase", mapOf("amount" to 100))
        assertEquals("purchase", event.name)
        assertEquals(100, event.params?.get("amount"))
    }

    @Test
    fun shouldCreateCustomEventWithNameOnly() {
        val event = AnalyticsEvent.Custom("app_open")
        assertEquals("app_open", event.name)
        assertNull(event.params)
    }
}
