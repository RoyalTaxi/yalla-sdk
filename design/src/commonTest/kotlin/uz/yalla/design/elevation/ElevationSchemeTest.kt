package uz.yalla.design.elevation

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ElevationSchemeTest {
    @Test
    fun standard_returnsExpectedValues() {
        val scheme = standardElevationScheme()
        assertEquals(0.dp, scheme.level0)
        assertEquals(1.dp, scheme.level1)
        assertEquals(3.dp, scheme.level2)
        assertEquals(6.dp, scheme.level3)
        assertEquals(8.dp, scheme.level4)
        assertEquals(12.dp, scheme.level5)
    }

    @Test
    fun structuralEquality_sameContent_areEqual() {
        assertEquals(standardElevationScheme(), standardElevationScheme())
        assertEquals(standardElevationScheme().hashCode(), standardElevationScheme().hashCode())
    }

    @Test
    fun structuralEquality_different_areNotEqual() {
        val a = standardElevationScheme()
        val b = a.copy(level3 = 10.dp)
        assertNotEquals(a, b)
    }
}
