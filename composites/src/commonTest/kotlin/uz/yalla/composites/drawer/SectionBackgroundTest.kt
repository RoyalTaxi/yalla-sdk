package uz.yalla.composites.drawer

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class SectionBackgroundColorsTest {

    private val colors = SectionBackgroundColors(background = Color.LightGray)

    @Test
    fun equality_sameValues_areEqual() {
        val other = SectionBackgroundColors(background = Color.LightGray)
        assertEquals(colors, other)
    }

    @Test
    fun equality_differentBackground_areNotEqual() {
        val other = SectionBackgroundColors(background = Color.White)
        assertNotEquals(colors, other)
    }

    @Test
    fun copy_overridesBackground() {
        val copied = colors.copy(background = Color.Red)
        assertEquals(Color.Red, copied.background)
    }

    @Test
    fun copy_withNoArgs_returnsEqualInstance() {
        assertEquals(colors, colors.copy())
    }
}

class SectionBackgroundDimensTest {

    @Test
    fun defaults_areNonNull() {
        val dimens = SectionBackgroundDefaults.dimens()
        assertNotNull(dimens)
    }

    @Test
    fun defaults_shape_isRoundedCorner16dp() {
        val dimens = SectionBackgroundDefaults.dimens()
        assertEquals(RoundedCornerShape(16.dp), dimens.shape)
    }

    @Test
    fun dimenEquality_sameShape_areEqual() {
        val a = SectionBackgroundDefaults.dimens()
        val b = SectionBackgroundDefaults.dimens()
        assertEquals(a, b)
    }

    @Test
    fun dimenEquality_differentShape_areNotEqual() {
        val a = SectionBackgroundDefaults.dimens()
        val b = SectionBackgroundDefaults.dimens(shape = RoundedCornerShape(8.dp))
        assertNotEquals(a, b)
    }

    @Test
    fun custom_shape_overridesDefault() {
        val customShape = RoundedCornerShape(8.dp)
        val dimens = SectionBackgroundDefaults.dimens(shape = customShape)
        assertEquals(customShape, dimens.shape)
    }

    @Test
    fun copy_overridesShape() {
        val original = SectionBackgroundDefaults.dimens()
        val newShape = RoundedCornerShape(4.dp)
        val copied = original.copy(shape = newShape)
        assertEquals(newShape, copied.shape)
    }

    @Test
    fun copy_withNoArgs_returnsEqualInstance() {
        val original = SectionBackgroundDefaults.dimens()
        assertEquals(original, original.copy())
    }
}
