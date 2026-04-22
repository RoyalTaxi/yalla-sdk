package uz.yalla.platform.haptic

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Smoke tests for [HapticType] enum and [HapticController] interface — support types
 * for the [rememberHapticController] expect/actual pair.
 *
 * The [rememberHapticController] composable itself requires a Compose UI harness that is
 * not wired in this module (Robolectric / Compose UI Test are absent). Those runtime
 * tests are deferred to Phase 4.
 *
 * These tests verify:
 * - The [HapticType] enum has exactly the seven documented variants.
 * - Each expected variant can be retrieved by name (compile-level API stability check).
 * - The [HapticController] interface is callable via an anonymous implementation.
 */
class HapticTypeTest {

    @Test
    fun shouldExposeSevenHapticVariants() {
        // The seven variants are part of the stable public API; their count must not shrink.
        assertEquals(7, HapticType.entries.size, "HapticType must have exactly 7 entries")
    }

    @Test
    fun shouldExposeAllDocumentedVariants() {
        // Each lookup by name confirms the enum constant is compiled and accessible.
        assertNotNull(HapticType.Light)
        assertNotNull(HapticType.Medium)
        assertNotNull(HapticType.Heavy)
        assertNotNull(HapticType.Success)
        assertNotNull(HapticType.Error)
        assertNotNull(HapticType.ErrorRepeat)
        assertNotNull(HapticType.Warning)
    }

    @Test
    fun shouldAllowHapticControllerImplementation() {
        // Verify the HapticController interface can be implemented: essential for fakes in tests.
        val receivedTypes = mutableListOf<HapticType>()
        val controller = object : HapticController {
            override fun perform(type: HapticType) {
                receivedTypes += type
            }
        }

        controller.perform(HapticType.Light)
        controller.perform(HapticType.Success)

        assertEquals(2, receivedTypes.size)
        assertEquals(HapticType.Light, receivedTypes[0])
        assertEquals(HapticType.Success, receivedTypes[1])
    }

    @Test
    fun shouldAllowHapticControllerToReceiveAllTypes() {
        // Iterate all HapticType entries to confirm the controller API is reachable for each.
        val received = mutableListOf<HapticType>()
        val controller = object : HapticController {
            override fun perform(type: HapticType) {
                received += type
            }
        }

        HapticType.entries.forEach { controller.perform(it) }

        assertTrue(
            received.containsAll(HapticType.entries),
            "All HapticType variants should be deliverable to HapticController",
        )
    }
}
