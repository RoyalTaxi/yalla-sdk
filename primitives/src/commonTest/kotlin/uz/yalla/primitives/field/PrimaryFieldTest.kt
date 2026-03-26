package uz.yalla.primitives.field

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class PrimaryFieldColorsTest {

    private val colors = PrimaryFieldColors(
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.DarkGray,
        focusedBorderColor = Color.Blue,
        unfocusedBorderColor = Color.LightGray,
        cursorColor = Color.Blue,
        selectionColors = TextSelectionColors(
            handleColor = Color.Blue,
            backgroundColor = Color.Blue.copy(alpha = 0.3f),
        ),
        placeholderColor = Color.Gray,
    )

    @Test
    fun equality_sameValues_areEqual() {
        val other = PrimaryFieldColors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.DarkGray,
            focusedBorderColor = Color.Blue,
            unfocusedBorderColor = Color.LightGray,
            cursorColor = Color.Blue,
            selectionColors = TextSelectionColors(
                handleColor = Color.Blue,
                backgroundColor = Color.Blue.copy(alpha = 0.3f),
            ),
            placeholderColor = Color.Gray,
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
        val copied = colors.copy(cursorColor = Color.Red)
        assertEquals(Color.Red, copied.cursorColor)
        assertEquals(colors.focusedTextColor, copied.focusedTextColor)
        assertEquals(colors.unfocusedBorderColor, copied.unfocusedBorderColor)
        assertEquals(colors.placeholderColor, copied.placeholderColor)
    }

    @Test
    fun copy_withNoArgs_returnsEqualInstance() {
        assertEquals(colors, colors.copy())
    }
}

class PrimaryFieldDimensTest {

    @Test
    fun defaults_shape_isRoundedCorner10dp() {
        val dimens = PrimaryFieldDefaults.dimens()
        assertEquals(RoundedCornerShape(10.dp), dimens.shape)
    }

    @Test
    fun customShape_overridesDefault() {
        val customShape = RoundedCornerShape(16.dp)
        val dimens = PrimaryFieldDefaults.dimens(shape = customShape)
        assertEquals(customShape, dimens.shape)
    }

    @Test
    fun copy_overridesOnlySpecifiedFields() {
        val original = PrimaryFieldDefaults.dimens()
        val copied = original.copy(shape = RoundedCornerShape(4.dp))
        assertEquals(RoundedCornerShape(4.dp), copied.shape)
    }

    @Test
    fun copy_withNoArgs_returnsEqualInstance() {
        val original = PrimaryFieldDefaults.dimens()
        assertEquals(original, original.copy())
    }
}
