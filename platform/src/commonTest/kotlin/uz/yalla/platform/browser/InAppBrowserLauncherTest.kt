package uz.yalla.platform.browser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Smoke tests for [InAppBrowserLauncher] (support type for [rememberInAppBrowser]).
 *
 * [rememberInAppBrowser] is a @Composable expect function — invoking it requires a
 * Compose UI harness (Robolectric / Compose UI Test) that is not wired in this module.
 * Those runtime tests are deferred to Phase 4.
 *
 * TODO(Phase 4): Add a Compose UI test that calls rememberInAppBrowser() inside a
 *   setContent block, verifies a non-null launcher is returned, and that open() does not
 *   throw for a valid HTTPS URL.
 *
 * These tests verify:
 * - [InAppBrowserLauncher] can be implemented by test doubles (essential for fakes).
 * - The [open] function signature is callable with a String argument.
 */
class InAppBrowserLauncherTest {

    @Test
    fun shouldAllowInAppBrowserLauncherImplementation() {
        // Verify the interface can be implemented — needed for fakes in consumer tests.
        val openedUrls = mutableListOf<String>()
        val launcher = object : InAppBrowserLauncher {
            override fun open(url: String) {
                openedUrls += url
            }
        }

        launcher.open("https://example.com")

        assertEquals(1, openedUrls.size)
        assertEquals("https://example.com", openedUrls[0])
    }

    @Test
    fun shouldAllowMultipleUrlsViaOpen() {
        val openedUrls = mutableListOf<String>()
        val launcher = object : InAppBrowserLauncher {
            override fun open(url: String) {
                openedUrls += url
            }
        }

        launcher.open("https://yalla.uz")
        launcher.open("https://royaltaxi.uz")

        assertEquals(2, openedUrls.size)
        assertNotNull(openedUrls.find { it.contains("yalla.uz") })
    }
}
