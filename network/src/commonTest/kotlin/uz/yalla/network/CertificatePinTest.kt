package uz.yalla.network

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

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
        assertFalse(pinPatternMatchesHost("*.example.com", "example.com"))
        assertFalse(pinPatternMatchesHost("*.example.com", "a.api.example.com"))
    }

    @Test
    fun anyDepthWildcardMatchesApexAndAnySubdomain() {
        assertTrue(pinPatternMatchesHost("**.example.com", "example.com"))
        assertTrue(pinPatternMatchesHost("**.example.com", "api.example.com"))
        assertTrue(pinPatternMatchesHost("**.example.com", "a.b.example.com"))
        assertFalse(pinPatternMatchesHost("**.example.com", "notexample.com"))
    }

    @Test
    fun emptyPinsDefaultDisablesPinningOnNetworkConfig() {
        assertTrue(
            NetworkConfig(
                baseUrl = "https://example.test/",
                brandId = "1",
                secretKey = "k"
            ).certificatePins.isEmpty()
        )
    }
}
