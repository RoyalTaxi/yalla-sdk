package uz.yalla.firebase.analytics

import kotlin.test.Test
import kotlin.test.assertNotNull

/**
 * Smoke tests for [YallaAnalytics].
 *
 * Integration-level tests that call into the real Firebase Analytics SDK are not feasible
 * in commonTest (no Firebase SDK available). These tests verify the class can be instantiated
 * in pure common code and that associated sealed types have the expected cases.
 */
class YallaAnalyticsTest {

    @Test
    fun shouldInstantiateWithoutThrowing() {
        // YallaAnalytics defers SDK access to the internal `analytics` lazy property;
        // construction itself must be safe without Firebase being initialized.
        val instance = YallaAnalytics()
        assertNotNull(instance)
    }

    @Test
    fun analyticsEventShouldHaveScreenViewCase() {
        val event: AnalyticsEvent = AnalyticsEvent.ScreenView("Home")
        assertNotNull(event)
    }

    @Test
    fun analyticsEventShouldHaveButtonClickCase() {
        val event: AnalyticsEvent = AnalyticsEvent.ButtonClick("confirm")
        assertNotNull(event)
    }

    @Test
    fun analyticsEventShouldHaveCustomCase() {
        val event: AnalyticsEvent = AnalyticsEvent.Custom("app_open")
        assertNotNull(event)
    }
}
