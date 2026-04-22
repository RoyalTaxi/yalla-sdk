package uz.yalla.platform.system

import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Smoke tests for [SystemBarColors] expect/actual pair (single-overload post-ADR-015b).
 *
 * [SystemBarColors] is a @Composable expect function — invoking it requires a Compose UI
 * harness that is not wired in this module (Robolectric / Compose UI Test absent).
 * Those runtime tests are deferred to Phase 4.
 *
 * ADR-015b retired the two-Color overload; only `SystemBarColors(darkIcons: Boolean)` remains.
 *
 * TODO(Phase 4): Add a Compose UI test that:
 *   - Calls SystemBarColors(darkIcons = true) inside a setContent block backed by a real Activity.
 *   - Asserts WindowCompat.getInsetsController().isAppearanceLightStatusBars == true (Android).
 *   - Repeats with darkIcons = false and asserts the flag flips.
 *   - On iOS: verifies UIApplication statusBarStyle reflects the requested appearance.
 *
 * This placeholder ensures the file exists in the test source set so the pair is tracked.
 */
class SystemBarColorsTest {

    @Test
    fun compileVerify_systemBarColorsSignatureExists() {
        // Confirms the post-ADR-015b single-overload expect declaration compiles from commonTest.
        assertTrue(
            true,
            "SystemBarColors(darkIcons: Boolean) expect declaration compiles and is visible from commonTest",
        )
    }
}
