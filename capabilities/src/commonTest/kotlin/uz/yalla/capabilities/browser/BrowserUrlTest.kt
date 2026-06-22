package uz.yalla.capabilities.browser

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

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
