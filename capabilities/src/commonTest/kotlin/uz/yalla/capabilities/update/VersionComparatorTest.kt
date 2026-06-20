package uz.yalla.capabilities.update

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Output-based characterization of [VersionComparator.isNewer].
 *
 * The load-bearing contract pinned here is **fail-closed on an undeterminable
 * installed version**: a blank or all-garbage installed version must return `false`
 * so the iOS force-update path can never fire off a missing
 * `CFBundleShortVersionString`. Also pins numeric (not lexical) segment ordering,
 * length-mismatch padding, and pre-release == release.
 */
class VersionComparatorTest {
    @Test
    fun blankInstalledVersionFailsClosed() {
        // Regression for the iOS fail-open force-update bug.
        assertFalse(VersionComparator.isNewer("1.0.0", ""))
    }

    @Test
    fun allGarbageInstalledVersionFailsClosed() {
        assertFalse(VersionComparator.isNewer("1.0.0", "abc"))
    }

    @Test
    fun storeNewerReturnsTrue() {
        assertTrue(VersionComparator.isNewer("1.0.1", "1.0.0"))
    }

    @Test
    fun storeOlderReturnsFalse() {
        assertFalse(VersionComparator.isNewer("1.0.0", "1.0.1"))
    }

    @Test
    fun equalVersionsReturnFalse() {
        assertFalse(VersionComparator.isNewer("1.2.3", "1.2.3"))
    }

    @Test
    fun shorterStoreVersionPadsWithZeros() {
        // "1.0" vs "1.0.0" → equal → not newer.
        assertFalse(VersionComparator.isNewer("1.0", "1.0.0"))
        // "1.0.1" vs "1.0" → newer.
        assertTrue(VersionComparator.isNewer("1.0.1", "1.0"))
    }

    @Test
    fun ordersSegmentsNumericallyNotLexically() {
        assertTrue(VersionComparator.isNewer("1.10", "1.9"))
        assertFalse(VersionComparator.isNewer("1.9", "1.10"))
    }

    @Test
    fun preReleaseSuffixComparesEqualToRelease() {
        // Non-numeric segment dropped: "1.2.0-beta" -> [1,2] == [1,2,0].
        assertFalse(VersionComparator.isNewer("1.2.0-beta", "1.2.0"))
    }
}
