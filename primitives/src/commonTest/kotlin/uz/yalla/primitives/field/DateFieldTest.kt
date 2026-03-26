package uz.yalla.primitives.field

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class DateFieldColorsTest {

    private val colors = DateFieldColors(
        containerColor = Color.Transparent,
        textColor = Color.Black,
        placeholderColor = Color.Gray,
        iconColor = Color.DarkGray,
    )

    @Test
    fun equality_sameValues_areEqual() {
        val other = DateFieldColors(
            containerColor = Color.Transparent,
            textColor = Color.Black,
            placeholderColor = Color.Gray,
            iconColor = Color.DarkGray,
        )
        assertEquals(colors, other)
    }

    @Test
    fun equality_differentValues_areNotEqual() {
        val other = colors.copy(textColor = Color.Red)
        assertNotEquals(colors, other)
    }

    @Test
    fun copy_overridesOnlySpecifiedFields() {
        val copied = colors.copy(iconColor = Color.Blue)
        assertEquals(Color.Blue, copied.iconColor)
        assertEquals(colors.containerColor, copied.containerColor)
        assertEquals(colors.textColor, copied.textColor)
        assertEquals(colors.placeholderColor, copied.placeholderColor)
    }

    @Test
    fun copy_withNoArgs_returnsEqualInstance() {
        assertEquals(colors, colors.copy())
    }
}

class DateFieldDimensTest {

    @Test
    fun defaults_shape_isRoundedCorner8dp() {
        val dimens = DateFieldDefaults.dimens()
        assertEquals(RoundedCornerShape(8.dp), dimens.shape)
    }

    @Test
    fun defaults_contentPadding_is16dp() {
        val dimens = DateFieldDefaults.dimens()
        assertEquals(PaddingValues(16.dp), dimens.contentPadding)
    }

    @Test
    fun customShape_overridesDefault() {
        val customShape = RoundedCornerShape(12.dp)
        val dimens = DateFieldDefaults.dimens(shape = customShape)
        assertEquals(customShape, dimens.shape)
        assertEquals(PaddingValues(16.dp), dimens.contentPadding)
    }

    @Test
    fun customContentPadding_overridesDefault() {
        val customPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
        val dimens = DateFieldDefaults.dimens(contentPadding = customPadding)
        assertEquals(customPadding, dimens.contentPadding)
        assertEquals(RoundedCornerShape(8.dp), dimens.shape)
    }

    @Test
    fun allCustomValues_overrideAllDefaults() {
        val customShape = RoundedCornerShape(4.dp)
        val customPadding = PaddingValues(8.dp)
        val dimens = DateFieldDefaults.dimens(
            shape = customShape,
            contentPadding = customPadding,
        )
        assertEquals(customShape, dimens.shape)
        assertEquals(customPadding, dimens.contentPadding)
    }

    @Test
    fun copy_overridesOnlySpecifiedFields() {
        val original = DateFieldDefaults.dimens()
        val copied = original.copy(shape = RoundedCornerShape(20.dp))
        assertEquals(RoundedCornerShape(20.dp), copied.shape)
        assertEquals(original.contentPadding, copied.contentPadding)
    }

    @Test
    fun copy_withNoArgs_returnsEqualInstance() {
        val original = DateFieldDefaults.dimens()
        assertEquals(original, original.copy())
    }
}
