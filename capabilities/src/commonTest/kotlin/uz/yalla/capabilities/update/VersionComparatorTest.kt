package uz.yalla.capabilities.update

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VersionComparatorTest {
    @Test
    fun blankInstalledVersionFailsClosed() {
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
        assertFalse(VersionComparator.isNewer("1.0", "1.0.0"))
        assertTrue(VersionComparator.isNewer("1.0.1", "1.0"))
    }

    @Test
    fun ordersSegmentsNumericallyNotLexically() {
        assertTrue(VersionComparator.isNewer("1.10", "1.9"))
        assertFalse(VersionComparator.isNewer("1.9", "1.10"))
    }

    @Test
    fun preReleaseSuffixComparesEqualToRelease() {
        assertFalse(VersionComparator.isNewer("1.2.0-beta", "1.2.0"))
    }
}
