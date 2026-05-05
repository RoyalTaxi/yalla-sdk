package uz.yalla.design.motion

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.time.Duration.Companion.milliseconds

class MotionSchemeTest {
    @Test
    fun standardMotionSchemeReturnsCanonicalDurations() {
        val scheme = standardMotionScheme()

        assertEquals(100.milliseconds, scheme.duration.instant)
        assertEquals(200.milliseconds, scheme.duration.quick)
        assertEquals(350.milliseconds, scheme.duration.standard)
        assertEquals(500.milliseconds, scheme.duration.slow)
        assertEquals(800.milliseconds, scheme.duration.contemplative)
    }

    @Test
    fun standardMotionSchemeReturnsCanonicalStaggerDelays() {
        val scheme = standardMotionScheme()

        assertEquals(30.milliseconds, scheme.stagger.list)
        assertEquals(50.milliseconds, scheme.stagger.grid)
        assertEquals(75.milliseconds, scheme.stagger.cards)
    }

    @Test
    fun standardMotionSchemeReturnsValueIdenticalInstancesAcrossCalls() {
        // standardMotionScheme is a factory, not a memoized singleton — but the
        // value content is stable. Equality holds; reference identity does not.
        val a = standardMotionScheme()
        val b = standardMotionScheme()

        assertEquals(a, b)
    }

    @Test
    fun durationEqualityHoldsForIdenticalContent() {
        val a =
            MotionScheme.Duration(
                instant = 100.milliseconds,
                quick = 200.milliseconds,
                standard = 350.milliseconds,
                slow = 500.milliseconds,
                contemplative = 800.milliseconds
            )
        val b =
            MotionScheme.Duration(
                instant = 100.milliseconds,
                quick = 200.milliseconds,
                standard = 350.milliseconds,
                slow = 500.milliseconds,
                contemplative = 800.milliseconds
            )

        assertEquals(a, b)
    }

    @Test
    fun durationDiffersWhenAnyFieldDiffers() {
        val base = standardMotionScheme().duration
        val tweaked = base.copy(quick = 250.milliseconds)

        assertNotEquals(base, tweaked)
    }

    @Test
    fun staggerEqualityHoldsForIdenticalContent() {
        val a =
            MotionScheme.Stagger(
                list = 30.milliseconds,
                grid = 50.milliseconds,
                cards = 75.milliseconds
            )
        val b =
            MotionScheme.Stagger(
                list = 30.milliseconds,
                grid = 50.milliseconds,
                cards = 75.milliseconds
            )

        assertEquals(a, b)
    }

    @Test
    fun motionSchemeEqualityHoldsAcrossStandardScheme() {
        assertEquals(standardMotionScheme(), standardMotionScheme())
    }

    @Test
    fun motionSchemeCopyDifferentiatesFromStandard() {
        val standard = standardMotionScheme()
        val tuned =
            standard.copy(
                duration = standard.duration.copy(quick = 220.milliseconds)
            )

        assertNotEquals(standard, tuned)
        assertEquals(220.milliseconds, tuned.duration.quick)
        assertEquals(standard.stagger, tuned.stagger)
    }
}
