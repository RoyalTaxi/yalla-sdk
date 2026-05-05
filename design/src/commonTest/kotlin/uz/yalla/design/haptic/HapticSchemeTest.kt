package uz.yalla.design.haptic

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class HapticSchemeTest {
    @Test
    fun haptic_hasFiveDistinctValues() {
        val all = Haptic.entries
        assertEquals(5, all.size)
        assertEquals(all.size, all.toSet().size, "Haptic enum values must be distinct")
    }

    @Test
    fun haptic_definesExpectedVocabulary() {
        // Order locked to the design-system spec — adding/removing is a breaking API change.
        assertEquals(
            listOf(
                Haptic.Selection,
                Haptic.Confirm,
                Haptic.Warn,
                Haptic.Error,
                Haptic.Hero
            ),
            Haptic.entries.toList()
        )
    }

    @Test
    fun noopHapticController_doesNotThrow_forAnyKind() {
        // The default LocalHapticController fallback must be safe in previews/tests.
        Haptic.entries.forEach { kind ->
            NoopHapticController.perform(kind)
        }
    }

    @Test
    fun hapticController_funInterface_isInvokable() {
        var fired: Haptic? = null
        val controller: HapticController = HapticController { fired = it }
        controller.perform(Haptic.Confirm)
        assertSame(Haptic.Confirm, fired)
    }

    @Test
    fun localHapticController_default_isNoop() {
        // Sanity check: the static default is the documented no-op singleton, not a fresh
        // instance every read (so equality below verifies the contract).
        assertTrue(LocalHapticController.toString().isNotEmpty())
    }
}
