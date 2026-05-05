package uz.yalla.design.stroke

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class StrokeSchemeTest {
    @Test
    fun standard_returnsExpectedValues() {
        val scheme = standardStrokeScheme()
        assertEquals(1.dp, scheme.hairline)
        assertEquals(1.5.dp, scheme.regular)
        assertEquals(2.dp, scheme.focus)
        assertEquals(2.dp, scheme.selected)
    }

    @Test
    fun structuralEquality_sameContent_areEqual() {
        assertEquals(standardStrokeScheme(), standardStrokeScheme())
        assertEquals(standardStrokeScheme().hashCode(), standardStrokeScheme().hashCode())
    }

    @Test
    fun structuralEquality_different_areNotEqual() {
        val a = standardStrokeScheme()
        val b = a.copy(focus = 3.dp)
        assertNotEquals(a, b)
    }
}
