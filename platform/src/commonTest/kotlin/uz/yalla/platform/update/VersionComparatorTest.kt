package uz.yalla.platform.update

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VersionComparatorTest {

    // --- Basic comparisons ---

    @Test
    fun shouldDetectNewerPatchVersion() {
        assertTrue(VersionComparator.isNewer("1.0.1", "1.0.0"))
    }

    @Test
    fun shouldDetectNewerMinorVersion() {
        assertTrue(VersionComparator.isNewer("1.1.0", "1.0.0"))
    }

    @Test
    fun shouldDetectNewerMajorVersion() {
        assertTrue(VersionComparator.isNewer("2.0.0", "1.9.9"))
    }

    @Test
    fun shouldReturnFalseOnSameVersion() {
        assertFalse(VersionComparator.isNewer("1.0.0", "1.0.0"))
    }

    @Test
    fun shouldReturnFalseOnOlderVersion() {
        assertFalse(VersionComparator.isNewer("1.0.0", "1.0.1"))
    }

    @Test
    fun shouldReturnFalseOnOlderMajorVersion() {
        assertFalse(VersionComparator.isNewer("1.9.9", "2.0.0"))
    }

    // --- Segment count mismatch ---

    @Test
    fun shouldTreatMissingSegmentsAsZero() {
        // "2.1" is equivalent to "2.1.0"
        assertFalse(VersionComparator.isNewer("2.1", "2.1.0"))
    }

    @Test
    fun shouldDetectNewerWhenStoreHasMoreSegments() {
        assertTrue(VersionComparator.isNewer("1.0.0.1", "1.0.0"))
    }

    @Test
    fun shouldDetectNewerWhenInstalledHasMoreSegments() {
        assertFalse(VersionComparator.isNewer("1.0.0", "1.0.0.1"))
    }

    @Test
    fun shouldHandleTwoSegmentVersions() {
        assertTrue(VersionComparator.isNewer("2.1", "2.0"))
        assertFalse(VersionComparator.isNewer("2.0", "2.1"))
    }

    @Test
    fun shouldHandleSingleSegmentVersions() {
        assertTrue(VersionComparator.isNewer("3", "2"))
        assertFalse(VersionComparator.isNewer("2", "3"))
    }

    // --- Edge cases ---

    @Test
    fun shouldReturnFalseOnBothEmpty() {
        // Both empty -> mapNotNull produces empty lists -> maxLen is 0 -> returns false
        assertFalse(VersionComparator.isNewer("", ""))
    }

    @Test
    fun shouldReturnFalseOnMalformedVersions() {
        // Non-numeric segments are filtered out by mapNotNull { toIntOrNull() }
        assertFalse(VersionComparator.isNewer("abc", "def"))
    }

    @Test
    fun shouldHandleMixedMalformedSegments() {
        // "1.abc.3" -> parts become [1, 3] (abc is dropped)
        // "1.2.3" -> parts become [1, 2, 3]
        // Compare: 1==1, 3>2 -> true (but this might be surprising behavior)
        val result = VersionComparator.isNewer("1.abc.3", "1.2.3")
        // [1, 3] vs [1, 2, 3]: i=0: 1==1, i=1: 3>2 -> true
        assertTrue(result)
    }

    @Test
    fun shouldHandleEmptyVsValidVersion() {
        // "" -> empty parts -> all segments treated as 0
        // "1.0.0" -> [1,0,0]
        // i=0: 0<1 -> false
        assertFalse(VersionComparator.isNewer("", "1.0.0"))
    }

    @Test
    fun shouldHandleValidVsEmptyVersion() {
        // "1.0.0" -> [1,0,0]
        // "" -> empty parts -> all segments treated as 0
        // i=0: 1>0 -> true
        assertTrue(VersionComparator.isNewer("1.0.0", ""))
    }

    // --- Real-world scenarios ---

    @Test
    fun shouldDetectNewerOnTypicalAppUpdate() {
        assertTrue(VersionComparator.isNewer("3.14.2", "3.14.1"))
    }

    @Test
    fun shouldDetectNewerOnMajorBump() {
        assertTrue(VersionComparator.isNewer("4.0.0", "3.99.99"))
    }
}
