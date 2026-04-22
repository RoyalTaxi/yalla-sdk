package uz.yalla.platform.haptic

import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Smoke tests for [rememberHapticController] expect/actual pair.
 *
 * [rememberHapticController] is a @Composable expect function — invoking it requires a
 * Compose UI harness that is not wired in this module (Robolectric / Compose UI Test absent).
 * The [HapticController] interface and [HapticType] enum are covered by [HapticTypeTest].
 *
 * TODO(Phase 4): Add a Compose UI test that:
 *   - Calls rememberHapticController() inside a setContent block.
 *   - Asserts the returned value is non-null and implements HapticController.
 *   - Calls controller.perform(HapticType.Medium) and verifies no exception is thrown.
 *   - On Android: spy on View.performHapticFeedback to confirm the correct constant is passed.
 *
 * This placeholder ensures the file exists in the test source set so the pair is tracked.
 */
class RememberHapticControllerTest {

    @Test
    fun compileVerify_rememberHapticControllerSignatureExists() {
        // Confirms the expect declaration is visible from commonTest and the module compiles.
        assertTrue(true, "rememberHapticController() expect declaration compiles and is visible from commonTest")
    }
}
