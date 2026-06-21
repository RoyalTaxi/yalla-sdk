package uz.yalla.network

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Pins the host-matching contract that both TLS-pinning engines rely on: [pinPatternMatchesHost] is the
 * single shared rule the Android engine applies directly and the Darwin engine mirrors. Exact hosts,
 * single-label `*.` and any-depth `**.` wildcards must behave like Ktor's Darwin `PinnedCertificate` so a
 * pin configured once in [NetworkConfig.certificatePins] covers the same hosts on both platforms.
 */
class CertificatePinTest {
    @Test
    fun exactHostMatchesOnlyItself() {
        assertTrue(pinPatternMatchesHost("api.example.com", "api.example.com"))
        assertFalse(pinPatternMatchesHost("api.example.com", "example.com"))
        assertFalse(pinPatternMatchesHost("api.example.com", "x.api.example.com"))
    }

    @Test
    fun matchingIsCaseInsensitive() {
        assertTrue(pinPatternMatchesHost("API.Example.com", "api.example.COM"))
    }

    @Test
    fun singleLabelWildcardMatchesExactlyOneLeftmostLabel() {
        assertTrue(pinPatternMatchesHost("*.example.com", "api.example.com"))
        // No prefix: a single-label wildcard requires exactly one label.
        assertFalse(pinPatternMatchesHost("*.example.com", "example.com"))
        // Two labels: more than one prefix is not matched by a single-label wildcard.
        assertFalse(pinPatternMatchesHost("*.example.com", "a.api.example.com"))
    }

    @Test
    fun anyDepthWildcardMatchesApexAndAnySubdomain() {
        assertTrue(pinPatternMatchesHost("**.example.com", "example.com"))
        assertTrue(pinPatternMatchesHost("**.example.com", "api.example.com"))
        assertTrue(pinPatternMatchesHost("**.example.com", "a.b.example.com"))
        // A different apex that merely ends in the same letters must not match.
        assertFalse(pinPatternMatchesHost("**.example.com", "notexample.com"))
    }

    @Test
    fun emptyPinsDefaultDisablesPinningOnNetworkConfig() {
        // The no-op default: an integrator who does not opt in keeps normal CA trust.
        assertTrue(
            NetworkConfig(
                baseUrl = "https://example.test/",
                brandId = "1",
                secretKey = "k"
            ).certificatePins.isEmpty()
        )
    }
}
