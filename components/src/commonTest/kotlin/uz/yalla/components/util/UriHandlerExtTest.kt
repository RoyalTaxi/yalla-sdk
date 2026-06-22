package uz.yalla.components.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

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
