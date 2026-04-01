package uz.yalla.media.picker

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class SelectionModeTest {
    // --- Single ---

    @Test
    fun singleShouldBeDataObject() {
        assertIs<SelectionMode.Single>(SelectionMode.Single)
    }

    @Test
    fun singleShouldBeSameInstance() {
        val a = SelectionMode.Single
        val b = SelectionMode.Single
        assertSame(a, b)
    }

    @Test
    fun singleShouldEqualItself() {
        assertEquals(SelectionMode.Single, SelectionMode.Single)
    }

    @Test
    fun singleShouldHaveConsistentHashCode() {
        assertEquals(SelectionMode.Single.hashCode(), SelectionMode.Single.hashCode())
    }

    @Test
    fun singleShouldBeSubtypeOfSelectionMode() {
        val mode: SelectionMode = SelectionMode.Single
        assertIs<SelectionMode>(mode)
    }

    // --- Multiple defaults ---

    @Test
    fun multipleDefaultShouldHaveInfinityMaxSelection() {
        val mode = SelectionMode.Multiple()
        assertEquals(SelectionMode.INFINITY, mode.maxSelection)
    }

    @Test
    fun multipleDefaultMaxSelectionShouldBeZero() {
        val mode = SelectionMode.Multiple()
        assertEquals(0, mode.maxSelection)
    }

    // --- Multiple custom ---

    @Test
    fun multipleShouldPreserveMaxSelection1() {
        val mode = SelectionMode.Multiple(maxSelection = 1)
        assertEquals(1, mode.maxSelection)
    }

    @Test
    fun multipleShouldPreserveMaxSelection10() {
        val mode = SelectionMode.Multiple(maxSelection = 10)
        assertEquals(10, mode.maxSelection)
    }

    @Test
    fun multipleShouldPreserveLargeMaxSelection() {
        val mode = SelectionMode.Multiple(maxSelection = Int.MAX_VALUE)
        assertEquals(Int.MAX_VALUE, mode.maxSelection)
    }

    @Test
    fun multipleShouldAllowNegativeMaxSelection() {
        // No validation in constructor — negative values are accepted
        val mode = SelectionMode.Multiple(maxSelection = -1)
        assertEquals(-1, mode.maxSelection)
    }

    // --- Multiple equality ---

    @Test
    fun multipleWithSameMaxSelectionShouldBeEqual() {
        val a = SelectionMode.Multiple(maxSelection = 5)
        val b = SelectionMode.Multiple(maxSelection = 5)
        assertEquals(a, b)
    }

    @Test
    fun multipleWithDifferentMaxSelectionShouldNotBeEqual() {
        val a = SelectionMode.Multiple(maxSelection = 5)
        val b = SelectionMode.Multiple(maxSelection = 10)
        assertNotEquals(a, b)
    }

    @Test
    fun multipleWithSameMaxSelectionShouldHaveSameHashCode() {
        val a = SelectionMode.Multiple(maxSelection = 5)
        val b = SelectionMode.Multiple(maxSelection = 5)
        assertEquals(a.hashCode(), b.hashCode())
    }

    // --- Multiple copy ---

    @Test
    fun multipleCopyShouldPreserveOriginalValues() {
        val original = SelectionMode.Multiple(maxSelection = 5)
        val copy = original.copy()
        assertEquals(5, copy.maxSelection)
    }

    @Test
    fun multipleCopyShouldAllowOverridingMaxSelection() {
        val original = SelectionMode.Multiple(maxSelection = 5)
        val copy = original.copy(maxSelection = 20)
        assertEquals(20, copy.maxSelection)
        assertEquals(5, original.maxSelection)
    }

    // --- Single vs Multiple ---

    @Test
    fun singleShouldNotEqualMultiple() {
        val single: SelectionMode = SelectionMode.Single
        val multiple: SelectionMode = SelectionMode.Multiple()
        assertNotEquals(single, multiple)
    }

    @Test
    fun singleAndMultipleShouldBeDifferentClasses() {
        val singleClass = SelectionMode.Single::class
        val multipleClass = SelectionMode.Multiple::class
        assertTrue(singleClass != multipleClass)
    }

    @Test
    fun shouldDistinguishViaWhenExpression() {
        val single: SelectionMode = SelectionMode.Single
        val multiple: SelectionMode = SelectionMode.Multiple(maxSelection = 3)

        val singleResult =
            when (single) {
                is SelectionMode.Single -> "single"
                is SelectionMode.Multiple -> "multiple"
            }
        val multipleResult =
            when (multiple) {
                is SelectionMode.Single -> "single"
                is SelectionMode.Multiple -> "multiple"
            }

        assertEquals("single", singleResult)
        assertEquals("multiple", multipleResult)
    }

    // --- INFINITY ---

    @Test
    fun infinityShouldBeZero() {
        assertEquals(0, SelectionMode.INFINITY)
    }

    @Test
    fun infinityShouldBeAccessibleFromCompanion() {
        assertTrue(SelectionMode.INFINITY >= 0)
    }

    // --- Multiple with INFINITY explicitly ---

    @Test
    fun multipleWithExplicitInfinityShouldEqualDefault() {
        val explicit = SelectionMode.Multiple(maxSelection = SelectionMode.INFINITY)
        val implicit = SelectionMode.Multiple()
        assertEquals(explicit, implicit)
    }
}
