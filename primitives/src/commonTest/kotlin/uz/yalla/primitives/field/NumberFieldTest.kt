package uz.yalla.primitives.field

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class NumberFieldColorsTest {

    private val colors = NumberFieldColors(
        containerColor = Color.White,
        textColor = Color.Black,
        placeholderColor = Color.Gray,
        prefixColor = Color.Black,
        dividerColor = Color.LightGray,
        focusedBorderColor = Color.Blue,
        unfocusedBorderColor = Color.LightGray,
        cursorColor = Color.Blue,
        selectionColors = TextSelectionColors(
            handleColor = Color.Blue,
            backgroundColor = Color.Blue.copy(alpha = 0.3f),
        ),
    )

    @Test
    fun equality_sameValues_areEqual() {
        val other = NumberFieldColors(
            containerColor = Color.White,
            textColor = Color.Black,
            placeholderColor = Color.Gray,
            prefixColor = Color.Black,
            dividerColor = Color.LightGray,
            focusedBorderColor = Color.Blue,
            unfocusedBorderColor = Color.LightGray,
            cursorColor = Color.Blue,
            selectionColors = TextSelectionColors(
                handleColor = Color.Blue,
                backgroundColor = Color.Blue.copy(alpha = 0.3f),
            ),
        )
        assertEquals(colors, other)
    }

    @Test
    fun equality_differentValues_areNotEqual() {
        val other = colors.copy(focusedBorderColor = Color.Red)
        assertNotEquals(colors, other)
    }

    @Test
    fun copy_overridesOnlySpecifiedFields() {
        val copied = colors.copy(prefixColor = Color.Red)
        assertEquals(Color.Red, copied.prefixColor)
        assertEquals(colors.containerColor, copied.containerColor)
        assertEquals(colors.textColor, copied.textColor)
        assertEquals(colors.dividerColor, copied.dividerColor)
        assertEquals(colors.focusedBorderColor, copied.focusedBorderColor)
    }

    @Test
    fun copy_withNoArgs_returnsEqualInstance() {
        assertEquals(colors, colors.copy())
    }
}

class NumberFieldDimensTest {

    @Test
    fun defaults_shape_isRoundedCorner10dp() {
        val dimens = NumberFieldDefaults.dimens()
        assertEquals(RoundedCornerShape(10.dp), dimens.shape)
    }

    @Test
    fun defaults_borderWidth_is1dp() {
        val dimens = NumberFieldDefaults.dimens()
        assertEquals(1.dp, dimens.borderWidth)
    }

    @Test
    fun defaults_dividerThickness_is1dp() {
        val dimens = NumberFieldDefaults.dimens()
        assertEquals(1.dp, dimens.dividerThickness)
    }

    @Test
    fun customShape_overridesDefault() {
        val customShape = RoundedCornerShape(16.dp)
        val dimens = NumberFieldDefaults.dimens(shape = customShape)
        assertEquals(customShape, dimens.shape)
        assertEquals(1.dp, dimens.borderWidth)
        assertEquals(1.dp, dimens.dividerThickness)
    }

    @Test
    fun allCustomValues_overrideAllDefaults() {
        val customShape = RoundedCornerShape(8.dp)
        val dimens = NumberFieldDefaults.dimens(
            shape = customShape,
            borderWidth = 2.dp,
            dividerThickness = 2.dp,
        )
        assertEquals(customShape, dimens.shape)
        assertEquals(2.dp, dimens.borderWidth)
        assertEquals(2.dp, dimens.dividerThickness)
    }

    @Test
    fun copy_overridesOnlySpecifiedFields() {
        val original = NumberFieldDefaults.dimens()
        val copied = original.copy(borderWidth = 3.dp)
        assertEquals(3.dp, copied.borderWidth)
        assertEquals(original.shape, copied.shape)
        assertEquals(original.dividerThickness, copied.dividerThickness)
    }

    @Test
    fun copy_withNoArgs_returnsEqualInstance() {
        val original = NumberFieldDefaults.dimens()
        assertEquals(original, original.copy())
    }
}
