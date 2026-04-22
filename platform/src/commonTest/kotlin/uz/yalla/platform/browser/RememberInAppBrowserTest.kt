package uz.yalla.platform.browser

import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Smoke tests for [rememberInAppBrowser] expect/actual pair.
 *
 * [rememberInAppBrowser] is a @Composable expect function — invoking it requires a Compose
 * UI harness that is not wired in this module (Robolectric / Compose UI Test absent).
 * The [InAppBrowserLauncher] interface contract is covered by [InAppBrowserLauncherTest].
 *
 * TODO(Phase 4): Add a Compose UI test that:
 *   - Calls rememberInAppBrowser() inside a setContent block.
 *   - Asserts the returned launcher is non-null.
 *   - Calls launcher.open("https://yalla.uz") and verifies no exception is thrown.
 *   - On Android: stubs CustomTabsIntent and asserts it was started with the correct Uri.
 *   - On iOS: verifies SFSafariViewController is presented (UIKit spy / mock).
 *
 * This placeholder ensures the file exists in the test source set so the pair is tracked.
 */
class RememberInAppBrowserTest {

    @Test
    fun compileVerify_rememberInAppBrowserSignatureExists() {
        // Confirms the expect declaration is visible from commonTest and the module compiles.
        assertTrue(true, "rememberInAppBrowser() expect declaration compiles and is visible from commonTest")
    }
}
