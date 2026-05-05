package uz.yalla.design.touchTarget

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class TouchTargetSchemeTest {
    @Test
    fun standard_returnsExpectedValues() {
        val scheme = standardTouchTargetScheme()
        assertEquals(48.dp, scheme.min)
        assertEquals(40.dp, scheme.compact)
    }

    @Test
    fun structuralEquality_sameContent_areEqual() {
        assertEquals(standardTouchTargetScheme(), standardTouchTargetScheme())
        assertEquals(standardTouchTargetScheme().hashCode(), standardTouchTargetScheme().hashCode())
    }

    @Test
    fun structuralEquality_different_areNotEqual() {
        val a = standardTouchTargetScheme()
        val b = a.copy(min = 56.dp)
        assertNotEquals(a, b)
    }
}
