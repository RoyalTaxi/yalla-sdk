package uz.yalla.network

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GuestModeGuardTest {
    private val allowed = DEFAULT_GUEST_ALLOWED_PATHS.toSet()

    @Test
    fun realOnboardingEndpointsAreAllowed() {
        assertTrue(isGuestAllowedPath("client", allowed))
        assertTrue(isGuestAllowedPath("valid", allowed))
        assertTrue(isGuestAllowedPath("register", allowed))
        assertTrue(isGuestAllowedPath("location-name", allowed))
        assertTrue(isGuestAllowedPath("address/tariff/cost", allowed))
        assertTrue(isGuestAllowedPath("executor/lists", allowed))
    }

    @Test
    fun leadingAndTrailingSlashesAreIgnored() {
        assertTrue(isGuestAllowedPath("/client", allowed))
        assertTrue(isGuestAllowedPath("/address/tariff/cost/", allowed))
    }

    @Test
    fun matchSurvivesABasePathPrefix() {
        assertTrue(isGuestAllowedPath("/api/v2/address/tariff/cost", allowed))
        assertTrue(isGuestAllowedPath("api/v1/executor/lists", allowed))
    }

    @Test
    fun authenticatedEndpointsAreBlocked() {
        assertFalse(isGuestAllowedPath("me", allowed))
        assertFalse(isGuestAllowedPath("order", allowed))
        assertFalse(isGuestAllowedPath("logout", allowed))
        assertFalse(isGuestAllowedPath("my/card/list", allowed))
        assertFalse(isGuestAllowedPath("client/addresses", allowed))
    }

    @Test
    fun multiSegmentEntriesNoLongerLeakOnTheirLastSegmentAlone() {
        assertFalse(isGuestAllowedPath("admin/cost", allowed))
        assertFalse(isGuestAllowedPath("report/daily/cost", allowed))
        assertFalse(isGuestAllowedPath("driver/lists", allowed))
    }

    @Test
    fun partialSegmentMatchesAreRejected() {
        assertFalse(isGuestAllowedPath("clients", allowed))
        assertFalse(isGuestAllowedPath("revalidate", allowed))
    }

    @Test
    fun emptyAllowListBlocksEverything() {
        assertFalse(isGuestAllowedPath("client", emptySet()))
    }
}
