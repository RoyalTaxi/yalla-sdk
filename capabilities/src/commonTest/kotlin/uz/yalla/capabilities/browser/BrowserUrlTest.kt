package uz.yalla.capabilities.browser

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Pins the shared [isWebUrl] allowlist both [Browser] actuals enforce: only
 * `http`/`https` (case-insensitive) are loadable; every other scheme and a missing
 * scheme are rejected, closing the scheme-confusion / open-redirect surface.
 */
class BrowserUrlTest {
    @Test
    fun acceptsHttpAndHttpsCaseInsensitively() {
        assertTrue(isWebUrl("http"))
        assertTrue(isWebUrl("https"))
        assertTrue(isWebUrl("HTTPS"))
    }

    @Test
    fun rejectsNonWebSchemes() {
        assertFalse(isWebUrl("file"))
        assertFalse(isWebUrl("intent"))
        assertFalse(isWebUrl("javascript"))
        assertFalse(isWebUrl("market"))
    }

    @Test
    fun rejectsMissingScheme() {
        assertFalse(isWebUrl(null))
        assertFalse(isWebUrl(""))
    }
}
