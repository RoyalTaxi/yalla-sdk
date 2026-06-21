package uz.yalla.components.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Pins the scheme allowlist on [normalizeWebUri]: backend/user-controlled links must only ever resolve
 * to `http`/`https`. Guards the security fix (finding M9) — a crafted `javascript:`/`file:`/`intent:`
 * link is rejected (returns null) rather than passed verbatim to the platform `UriHandler`.
 */
class UriHandlerExtTest {
    @Test
    fun prependsHttpsWhenSchemeMissing() {
        assertEquals("https://yalla.uz/trip/42", normalizeWebUri("yalla.uz/trip/42"))
    }

    @Test
    fun keepsAllowedHttpAndHttpsSchemes() {
        assertEquals("http://yalla.uz", normalizeWebUri("http://yalla.uz"))
        assertEquals("https://yalla.uz", normalizeWebUri("https://yalla.uz"))
    }

    @Test
    fun allowlistIsCaseInsensitiveOnScheme() {
        assertEquals("HTTPS://yalla.uz", normalizeWebUri("HTTPS://yalla.uz"))
    }

    @Test
    fun rejectsJavascriptScheme() {
        assertNull(normalizeWebUri("javascript:alert(1)"))
    }

    @Test
    fun rejectsFileScheme() {
        assertNull(normalizeWebUri("file:///etc/passwd"))
    }

    @Test
    fun rejectsContentScheme() {
        assertNull(normalizeWebUri("content://com.evil/secret"))
    }

    @Test
    fun rejectsIntentScheme() {
        assertNull(normalizeWebUri("intent://evil#Intent;scheme=foo;end"))
    }

    @Test
    fun rejectsCustomAppScheme() {
        assertNull(normalizeWebUri("yallaapp://pay?to=attacker"))
    }

    @Test
    fun returnsNullForBlankInput() {
        assertNull(normalizeWebUri("   "))
    }
}
