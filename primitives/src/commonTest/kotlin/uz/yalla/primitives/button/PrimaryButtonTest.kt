package uz.yalla.primitives.button

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class PrimaryButtonColorsTest {

    private val colors = PrimaryButtonColors(
        containerColor = Color.Red,
        contentColor = Color.White,
        disabledContainerColor = Color.Gray,
        disabledContentColor = Color.DarkGray,
    )

    @Test
    fun containerColor_whenEnabled_returnsContainerColor() {
        assertEquals(Color.Red, colors.containerColor(enabled = true))
    }

    @Test
    fun containerColor_whenDisabled_returnsDisabledContainerColor() {
        assertEquals(Color.Gray, colors.containerColor(enabled = false))
    }

    @Test
    fun contentColor_whenEnabled_returnsContentColor() {
        assertEquals(Color.White, colors.contentColor(enabled = true))
    }

    @Test
    fun contentColor_whenDisabled_returnsDisabledContentColor() {
        assertEquals(Color.DarkGray, colors.contentColor(enabled = false))
    }

    @Test
    fun enabledAndDisabledContainerColors_areDifferent() {
        assertNotEquals(colors.containerColor(enabled = true), colors.containerColor(enabled = false))
    }

    @Test
    fun enabledAndDisabledContentColors_areDifferent() {
        assertNotEquals(colors.contentColor(enabled = true), colors.contentColor(enabled = false))
    }

    @Test
    fun equality_sameValues_areEqual() {
        val other = PrimaryButtonColors(
            containerColor = Color.Red,
            contentColor = Color.White,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.DarkGray,
        )
        assertEquals(colors, other)
    }

    @Test
    fun equality_differentValues_areNotEqual() {
        val other = colors.copy(containerColor = Color.Blue)
        assertNotEquals(colors, other)
    }

    @Test
    fun copy_overridesOnlySpecifiedFields() {
        val copied = colors.copy(containerColor = Color.Blue)
        assertEquals(Color.Blue, copied.containerColor)
        assertEquals(colors.contentColor, copied.contentColor)
        assertEquals(colors.disabledContainerColor, copied.disabledContainerColor)
        assertEquals(colors.disabledContentColor, copied.disabledContentColor)
    }

    @Test
    fun copy_withNoArgs_returnsEqualInstance() {
        assertEquals(colors, colors.copy())
    }
}

class PrimaryButtonDimensTest {

    @Test
    fun defaults_minHeight_is60dp() {
        val dimens = PrimaryButtonDefaults.dimens()
        assertEquals(60.dp, dimens.minHeight)
    }

    @Test
    fun defaults_contentPadding_matchesExpected() {
        val dimens = PrimaryButtonDefaults.dimens()
        assertEquals(PaddingValues(horizontal = 24.dp, vertical = 16.dp), dimens.contentPadding)
    }

    @Test
    fun defaults_shape_isRoundedCorner16dp() {
        val dimens = PrimaryButtonDefaults.dimens()
        assertEquals(RoundedCornerShape(16.dp), dimens.shape)
    }

    @Test
    fun defaults_iconSpacing_is8dp() {
        val dimens = PrimaryButtonDefaults.dimens()
        assertEquals(8.dp, dimens.iconSpacing)
    }

    @Test
    fun customMinHeight_overridesDefault() {
        val dimens = PrimaryButtonDefaults.dimens(minHeight = 48.dp)
        assertEquals(48.dp, dimens.minHeight)
        // Other defaults remain unchanged
        assertEquals(8.dp, dimens.iconSpacing)
        assertEquals(RoundedCornerShape(16.dp), dimens.shape)
    }

    @Test
    fun customShape_overridesDefault() {
        val customShape = RoundedCornerShape(8.dp)
        val dimens = PrimaryButtonDefaults.dimens(shape = customShape)
        assertEquals(customShape, dimens.shape)
        assertEquals(60.dp, dimens.minHeight)
    }

    @Test
    fun customIconSpacing_overridesDefault() {
        val dimens = PrimaryButtonDefaults.dimens(iconSpacing = 12.dp)
        assertEquals(12.dp, dimens.iconSpacing)
    }

    @Test
    fun allCustomValues_overrideAllDefaults() {
        val customPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        val customShape = RoundedCornerShape(4.dp)
        val dimens = PrimaryButtonDefaults.dimens(
            minHeight = 40.dp,
            contentPadding = customPadding,
            shape = customShape,
            iconSpacing = 4.dp,
        )
        assertEquals(40.dp, dimens.minHeight)
        assertEquals(customPadding, dimens.contentPadding)
        assertEquals(customShape, dimens.shape)
        assertEquals(4.dp, dimens.iconSpacing)
    }

    @Test
    fun copy_overridesOnlySpecifiedFields() {
        val original = PrimaryButtonDefaults.dimens()
        val copied = original.copy(minHeight = 44.dp)
        assertEquals(44.dp, copied.minHeight)
        assertEquals(original.contentPadding, copied.contentPadding)
        assertEquals(original.shape, copied.shape)
        assertEquals(original.iconSpacing, copied.iconSpacing)
    }

    @Test
    fun copy_withNoArgs_returnsEqualInstance() {
        val original = PrimaryButtonDefaults.dimens()
        assertEquals(original, original.copy())
    }

    @Test
    fun defaultConstants_matchDimensFactoryDefaults() {
        val dimens = PrimaryButtonDefaults.dimens()
        assertEquals(PrimaryButtonDefaults.MinHeight, dimens.minHeight)
        assertEquals(PrimaryButtonDefaults.ContentPadding, dimens.contentPadding)
        assertEquals(PrimaryButtonDefaults.Shape, dimens.shape)
    }
}
