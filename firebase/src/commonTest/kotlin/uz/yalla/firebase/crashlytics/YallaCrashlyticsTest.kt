package uz.yalla.firebase.crashlytics

import kotlin.test.Test
import kotlin.test.assertNotNull

/**
 * Smoke tests for [YallaCrashlytics].
 *
 * Integration-level tests that call into the real Firebase Crashlytics SDK are not feasible
 * in commonTest (no Firebase SDK available). These tests verify the class can be instantiated
 * in pure common code without touching platform services.
 */
class YallaCrashlyticsTest {

    @Test
    fun shouldInstantiateWithoutThrowing() {
        // YallaCrashlytics defers SDK access to the internal `crashlytics` lazy property;
        // construction itself must be safe without Firebase being initialized.
        val instance = YallaCrashlytics()
        assertNotNull(instance)
    }
}
