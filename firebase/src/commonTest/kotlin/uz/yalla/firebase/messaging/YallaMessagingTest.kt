package uz.yalla.firebase.messaging

import kotlin.test.Test
import kotlin.test.assertNotNull

/**
 * Smoke tests for [YallaMessaging].
 *
 * Integration-level tests that call into the real Firebase Messaging SDK are not feasible
 * in commonTest (no Firebase SDK available). These tests verify the class can be instantiated
 * in pure common code without touching platform services.
 */
class YallaMessagingTest {

    @Test
    fun shouldInstantiateWithoutThrowing() {
        // YallaMessaging defers SDK access to the internal `messaging` lazy property;
        // construction itself must be safe without Firebase being initialized.
        val instance = YallaMessaging()
        assertNotNull(instance)
    }

    @Test
    fun messagingDelegateShouldBeImplementable() {
        // Verify the interface contract is stable and can be satisfied in common code
        val delegate = object : MessagingDelegate {
            override fun onNewToken(token: String) = Unit

            override fun onMessageReceived(title: String?, body: String?, data: Map<String, String>) = Unit
        }
        assertNotNull(delegate)
    }
}
