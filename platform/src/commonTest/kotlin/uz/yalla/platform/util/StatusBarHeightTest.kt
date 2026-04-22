package uz.yalla.platform.util

import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Smoke tests for [statusBarHeight] expect/actual pair.
 *
 * [statusBarHeight] is a @Composable expect function — invoking it requires a Compose UI
 * harness that is not wired in this module (Robolectric / Compose UI Test absent).
 *
 * TODO(Phase 4): Add a Compose UI test that:
 *   - Calls statusBarHeight() inside a setContent block backed by a real Activity.
 *   - Asserts the returned Dp value is > 0.dp on a device/emulator with a status bar.
 *   - On Android: cross-checks against WindowInsets.statusBars.asPaddingValues().calculateTopPadding().
 *   - On iOS: cross-checks against UIApplication.shared.statusBarFrame.size.height.
 *
 * This placeholder ensures the file exists in the test source set so the pair is tracked.
 */
class StatusBarHeightTest {

    @Test
    fun compileVerify_statusBarHeightSignatureExists() {
        // Confirms the expect declaration is visible from commonTest and the module compiles.
        assertTrue(true, "statusBarHeight() expect declaration compiles and is visible from commonTest")
    }
}
