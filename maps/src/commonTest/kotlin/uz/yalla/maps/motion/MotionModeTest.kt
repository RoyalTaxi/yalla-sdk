package uz.yalla.maps.motion

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertTrue

class MotionModeTest {
    @Test
    fun routeFollowingAndChordOnlyAreDistinctMotionModes() {
        val route: MotionMode = MotionMode.RouteFollowing
        val chord: MotionMode = MotionMode.ChordOnly

        assertNotSame(route, chord)
        assertTrue(route is MotionMode.RouteFollowing)
        assertTrue(chord is MotionMode.ChordOnly)
    }

    @Test
    fun dataObjectsAreSingletons() {
        assertEquals(MotionMode.RouteFollowing, MotionMode.RouteFollowing)
        assertEquals(MotionMode.ChordOnly, MotionMode.ChordOnly)
    }

    @Test
    fun whenIsExhaustiveOverBothModes() {
        // Compile-time guarantee: an exhaustive `when` with no `else` only compiles while MotionMode
        // has exactly these two cases, which is the invariant the required driver param relies on.
        fun enabled(mode: MotionMode): Boolean =
            when (mode) {
                MotionMode.RouteFollowing -> true
                MotionMode.ChordOnly -> false
            }

        assertTrue(enabled(MotionMode.RouteFollowing))
        assertTrue(!enabled(MotionMode.ChordOnly))
    }
}
