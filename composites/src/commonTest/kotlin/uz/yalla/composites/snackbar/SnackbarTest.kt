package uz.yalla.composites.snackbar

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class SnackbarColorsTest {

    private val colors = SnackbarColors(
        container = Color.Green,
        iconBackground = Color.White,
        icon = Color.Black,
        text = Color.Black,
        dismissIcon = Color.Gray,
    )

    @Test
    fun equality_sameValues_areEqual() {
        val other = SnackbarColors(
            container = Color.Green,
            iconBackground = Color.White,
            icon = Color.Black,
            text = Color.Black,
            dismissIcon = Color.Gray,
        )
        assertEquals(colors, other)
    }

    @Test
    fun equality_differentContainer_areNotEqual() {
        val other = colors.copy(container = Color.Red)
        assertNotEquals(colors, other)
    }

    @Test
    fun equality_differentIconBackground_areNotEqual() {
        val other = colors.copy(iconBackground = Color.LightGray)
        assertNotEquals(colors, other)
    }

    @Test
    fun equality_differentText_areNotEqual() {
        val other = colors.copy(text = Color.Blue)
        assertNotEquals(colors, other)
    }

    @Test
    fun copy_overridesOnlySpecifiedFields() {
        val copied = colors.copy(dismissIcon = Color.Red)
        assertEquals(Color.Red, copied.dismissIcon)
        assertEquals(colors.container, copied.container)
        assertEquals(colors.iconBackground, copied.iconBackground)
        assertEquals(colors.icon, copied.icon)
        assertEquals(colors.text, copied.text)
    }

    @Test
    fun copy_withNoArgs_returnsEqualInstance() {
        assertEquals(colors, colors.copy())
    }
}

class SnackbarDimensTest {

    @Test
    fun defaults_areNonNull() {
        val dimens = SnackbarDefaults.dimens()
        assertNotNull(dimens)
    }

    @Test
    fun defaults_contentSpacing_is8dp() {
        val dimens = SnackbarDefaults.dimens()
        assertEquals(8.dp, dimens.contentSpacing)
    }

    @Test
    fun defaults_verticalPadding_is12dp() {
        val dimens = SnackbarDefaults.dimens()
        assertEquals(12.dp, dimens.verticalPadding)
    }

    @Test
    fun defaults_horizontalPadding_is16dp() {
        val dimens = SnackbarDefaults.dimens()
        assertEquals(16.dp, dimens.horizontalPadding)
    }

    @Test
    fun defaults_iconSize_is24dp() {
        val dimens = SnackbarDefaults.dimens()
        assertEquals(24.dp, dimens.iconSize)
    }

    @Test
    fun defaults_iconPadding_is6dp() {
        val dimens = SnackbarDefaults.dimens()
        assertEquals(6.dp, dimens.iconPadding)
    }

    @Test
    fun defaults_iconBackgroundRadius_is36dp() {
        val dimens = SnackbarDefaults.dimens()
        assertEquals(36.dp, dimens.iconBackgroundRadius)
    }

    @Test
    fun defaults_dismissIconSize_is20dp() {
        val dimens = SnackbarDefaults.dimens()
        assertEquals(20.dp, dimens.dismissIconSize)
    }

    @Test
    fun defaults_messageMaxLines_is2() {
        val dimens = SnackbarDefaults.dimens()
        assertEquals(2, dimens.messageMaxLines)
    }

    @Test
    fun defaults_shape_isRoundedCorner12dp() {
        val dimens = SnackbarDefaults.dimens()
        assertEquals(RoundedCornerShape(12.dp), dimens.shape)
    }

    @Test
    fun custom_messageMaxLines_overridesDefault() {
        val dimens = SnackbarDefaults.dimens(messageMaxLines = 1)
        assertEquals(1, dimens.messageMaxLines)
        assertEquals(8.dp, dimens.contentSpacing)
    }

    @Test
    fun custom_iconSize_overridesDefault() {
        val dimens = SnackbarDefaults.dimens(iconSize = 32.dp)
        assertEquals(32.dp, dimens.iconSize)
        assertEquals(20.dp, dimens.dismissIconSize)
    }

    @Test
    fun dimenEquality_sameValues_areEqual() {
        val a = SnackbarDefaults.dimens()
        val b = SnackbarDefaults.dimens()
        assertEquals(a, b)
    }

    @Test
    fun dimenEquality_differentValues_areNotEqual() {
        val a = SnackbarDefaults.dimens()
        val b = SnackbarDefaults.dimens(contentSpacing = 16.dp)
        assertNotEquals(a, b)
    }

    @Test
    fun copy_overridesOnlySpecifiedFields() {
        val original = SnackbarDefaults.dimens()
        val copied = original.copy(dismissIconSize = 24.dp)
        assertEquals(24.dp, copied.dismissIconSize)
        assertEquals(original.contentSpacing, copied.contentSpacing)
        assertEquals(original.verticalPadding, copied.verticalPadding)
        assertEquals(original.horizontalPadding, copied.horizontalPadding)
        assertEquals(original.iconSize, copied.iconSize)
        assertEquals(original.messageMaxLines, copied.messageMaxLines)
    }
}
