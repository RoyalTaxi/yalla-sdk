package uz.yalla.design.radius

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class RadiusSchemeTest {

    @Test
    fun standard_returnsExpectedValues() {
        val scheme = standardRadiusScheme()
        assertEquals(4.dp, scheme.xs)
        assertEquals(8.dp, scheme.s)
        assertEquals(12.dp, scheme.m)
        assertEquals(16.dp, scheme.l)
        assertEquals(24.dp, scheme.xl)
        assertEquals(40.dp, scheme.sheet)
    }

    @Test
    fun structuralEquality_sameContent_areEqual() {
        assertEquals(standardRadiusScheme(), standardRadiusScheme())
        assertEquals(standardRadiusScheme().hashCode(), standardRadiusScheme().hashCode())
    }

    @Test
    fun structuralEquality_different_areNotEqual() {
        val a = standardRadiusScheme()
        val b = a.copy(l = 20.dp)
        assertNotEquals(a, b)
    }
}
