package uz.yalla.primitives.otp

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class PinRowColorsTest {

    private val colors = PinRowColors(
        textColor = Color.Black,
        errorTextColor = Color.Red,
        filledBorderColor = Color.Blue,
        emptyBorderColor = Color.LightGray,
        errorBorderColor = Color.Red,
    )

    @Test
    fun equality_sameValues_areEqual() {
        val other = PinRowColors(
            textColor = Color.Black,
            errorTextColor = Color.Red,
            filledBorderColor = Color.Blue,
            emptyBorderColor = Color.LightGray,
            errorBorderColor = Color.Red,
        )
        assertEquals(colors, other)
    }

    @Test
    fun equality_differentValues_areNotEqual() {
        val other = colors.copy(textColor = Color.White)
        assertNotEquals(colors, other)
    }

    @Test
    fun copy_overridesOnlySpecifiedFields() {
        val copied = colors.copy(filledBorderColor = Color.Green)
        assertEquals(Color.Green, copied.filledBorderColor)
        assertEquals(colors.textColor, copied.textColor)
        assertEquals(colors.errorTextColor, copied.errorTextColor)
        assertEquals(colors.emptyBorderColor, copied.emptyBorderColor)
        assertEquals(colors.errorBorderColor, copied.errorBorderColor)
    }

    @Test
    fun copy_withNoArgs_returnsEqualInstance() {
        assertEquals(colors, colors.copy())
    }
}

class PinRowDimensTest {

    @Test
    fun defaults_shape_isRoundedCorner12dp() {
        val dimens = PinRowDefaults.dimens()
        assertEquals(RoundedCornerShape(12.dp), dimens.shape)
    }

    @Test
    fun defaults_spacing_is8dp() {
        val dimens = PinRowDefaults.dimens()
        assertEquals(8.dp, dimens.spacing)
    }

    @Test
    fun defaults_borderWidth_is1dp() {
        val dimens = PinRowDefaults.dimens()
        assertEquals(1.dp, dimens.borderWidth)
    }

    @Test
    fun customShape_overridesDefault() {
        val customShape = RoundedCornerShape(8.dp)
        val dimens = PinRowDefaults.dimens(shape = customShape)
        assertEquals(customShape, dimens.shape)
        assertEquals(8.dp, dimens.spacing)
        assertEquals(1.dp, dimens.borderWidth)
    }

    @Test
    fun customSpacing_overridesDefault() {
        val dimens = PinRowDefaults.dimens(spacing = 12.dp)
        assertEquals(12.dp, dimens.spacing)
        assertEquals(RoundedCornerShape(12.dp), dimens.shape)
    }

    @Test
    fun allCustomValues_overrideAllDefaults() {
        val customShape = RoundedCornerShape(4.dp)
        val dimens = PinRowDefaults.dimens(
            shape = customShape,
            spacing = 4.dp,
            borderWidth = 2.dp,
        )
        assertEquals(customShape, dimens.shape)
        assertEquals(4.dp, dimens.spacing)
        assertEquals(2.dp, dimens.borderWidth)
    }

    @Test
    fun copy_overridesOnlySpecifiedFields() {
        val original = PinRowDefaults.dimens()
        val copied = original.copy(spacing = 16.dp)
        assertEquals(16.dp, copied.spacing)
        assertEquals(original.shape, copied.shape)
        assertEquals(original.borderWidth, copied.borderWidth)
    }

    @Test
    fun copy_withNoArgs_returnsEqualInstance() {
        val original = PinRowDefaults.dimens()
        assertEquals(original, original.copy())
    }
}
