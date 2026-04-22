package uz.yalla.platform.otp

import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Smoke tests for [getAppSignature] expect/actual pair.
 *
 * The context-free overload must return a nullable String on every platform.
 * On iOS: always null (no hash needed — system handles OTP natively).
 * On Android: null from the context-free actual (real hash requires a Context overload).
 */
class AppSignatureTest {

    @Test
    fun shouldReturnNullOrStringOnEveryPlatform() {
        val result: String? = getAppSignature()
        // Result must be String? — this is a compile-only assertion for the nullable contract.
        // Both Android and iOS actuals return null for the no-context overload.
        assertNull(result)
    }

    @Test
    fun shouldNotThrowWhenCalled() {
        // Verify no exception escapes the expect/actual pair regardless of platform.
        val result = runCatching { getAppSignature() }
        assertTrue(result.isSuccess, "getAppSignature() threw unexpectedly: ${result.exceptionOrNull()}")
    }
}
