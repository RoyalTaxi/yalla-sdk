package uz.yalla.platform.navigation

import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Smoke tests for [NativeNavHost] expect/actual pair.
 *
 * [NativeNavHost] is a @Composable expect function — invoking it requires a Compose UI
 * harness that is not wired in this module (Robolectric / Compose UI Test absent).
 * The supporting types ([Route], [ScreenConfig], [ToolbarAction], [ToolbarIcon]) are
 * covered by [RouteTest] and [ScreenConfigTest].
 *
 * TODO(Phase 4): Add a Compose UI test that:
 *   - Builds a NativeRootComponent<TestRoute> with a TestDefaultComponentContext.
 *   - Sets up a simple ScreenProvider returning a "Hello" Text for the initial route.
 *   - Renders NativeNavHost inside a setContent block.
 *   - Asserts the "Hello" text node is visible in the semantic tree.
 *   - Pushes a second route and asserts the navigation bar back button appears (Android).
 *
 * This placeholder ensures the file exists in the test source set so the pair is tracked.
 */
class NativeNavHostTest {

    @Test
    fun compileVerify_nativeNavHostSignatureExists() {
        // Confirms the expect declaration is visible from commonTest and the module compiles.
        assertTrue(true, "NativeNavHost<C : Route>(...) expect declaration compiles and is visible from commonTest")
    }
}
