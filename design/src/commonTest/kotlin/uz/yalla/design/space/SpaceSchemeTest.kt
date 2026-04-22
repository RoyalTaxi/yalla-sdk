package uz.yalla.design.space

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class SpaceSchemeTest {

    @Test
    fun standard_returnsExpectedSemanticValues() {
        val scheme = standardSpaceScheme()
        assertEquals(20.dp, scheme.screenEdge)
        assertEquals(20.dp, scheme.sheetEdge)
        assertEquals(16.dp, scheme.contentEdge)
        assertEquals(12.dp, scheme.itemGap)
        assertEquals(32.dp, scheme.sectionGap)
        assertEquals(56.dp, scheme.heroGap)
        assertEquals(8.dp, scheme.inlineGap)
    }

    @Test
    fun standard_returnsExpectedScaleValues() {
        val scale = standardSpaceScheme().scale
        assertEquals(2.dp, scale.xxs)
        assertEquals(4.dp, scale.xs)
        assertEquals(8.dp, scale.s)
        assertEquals(12.dp, scale.m)
        assertEquals(16.dp, scale.l)
        assertEquals(20.dp, scale.xl)
        assertEquals(24.dp, scale.xxl)
        assertEquals(32.dp, scale.huge)
        assertEquals(40.dp, scale.section)
        assertEquals(56.dp, scale.massive)
    }

    @Test
    fun structuralEquality_sameContent_areEqual() {
        assertEquals(standardSpaceScheme(), standardSpaceScheme())
        assertEquals(standardSpaceScheme().hashCode(), standardSpaceScheme().hashCode())
    }

    @Test
    fun structuralEquality_differentSemantic_areNotEqual() {
        val a = standardSpaceScheme()
        val b = a.copy(screenEdge = 24.dp)
        assertNotEquals(a, b)
    }

    @Test
    fun structuralEquality_differentScale_areNotEqual() {
        val a = standardSpaceScheme()
        val b = a.copy(scale = a.scale.copy(xl = 99.dp))
        assertNotEquals(a, b)
    }
}
