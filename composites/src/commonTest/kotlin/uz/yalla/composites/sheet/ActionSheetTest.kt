package uz.yalla.composites.sheet

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ActionSheetColorsTest {

    private val colors = ActionSheetColors(
        container = Color.White,
        title = Color.Black,
        message = Color.Gray,
        divider = Color.LightGray,
    )

    @Test
    fun equality_sameValues_areEqual() {
        val other = ActionSheetColors(
            container = Color.White,
            title = Color.Black,
            message = Color.Gray,
            divider = Color.LightGray,
        )
        assertEquals(colors, other)
    }

    @Test
    fun equality_differentValues_areNotEqual() {
        val other = colors.copy(container = Color.Red)
        assertNotEquals(colors, other)
    }

    @Test
    fun copy_overridesOnlySpecifiedFields() {
        val copied = colors.copy(title = Color.Blue)
        assertEquals(Color.Blue, copied.title)
        assertEquals(colors.container, copied.container)
        assertEquals(colors.message, copied.message)
        assertEquals(colors.divider, copied.divider)
    }
}

class ActionSheetDimensTest {

    @Test
    fun defaults_haveExpectedValues() {
        val dimens = ActionSheetDefaults.dimens()
        assertEquals(RoundedCornerShape(topStart = 38.dp, topEnd = 38.dp), dimens.shape)
        assertEquals(PaddingValues(horizontal = 20.dp), dimens.contentPadding)
        assertEquals(16.dp, dimens.topSpacing)
        assertEquals(8.dp, dimens.titleMessageSpacing)
        assertEquals(24.dp, dimens.contentButtonsSpacing)
        assertEquals(16.dp, dimens.dividerButtonSpacing)
        assertEquals(8.dp, dimens.buttonSpacing)
        assertEquals(12.dp, dimens.bottomSpacing)
        assertEquals(1.dp, dimens.dividerThickness)
    }

    @Test
    fun dimens_overridesWork() {
        val dimens = ActionSheetDefaults.dimens(
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(24.dp),
            topSpacing = 20.dp,
            titleMessageSpacing = 12.dp,
            contentButtonsSpacing = 32.dp,
            dividerButtonSpacing = 20.dp,
            buttonSpacing = 12.dp,
            bottomSpacing = 16.dp,
            dividerThickness = 2.dp,
        )
        assertEquals(RoundedCornerShape(16.dp), dimens.shape)
        assertEquals(PaddingValues(24.dp), dimens.contentPadding)
        assertEquals(20.dp, dimens.topSpacing)
        assertEquals(12.dp, dimens.titleMessageSpacing)
        assertEquals(32.dp, dimens.contentButtonsSpacing)
        assertEquals(20.dp, dimens.dividerButtonSpacing)
        assertEquals(12.dp, dimens.buttonSpacing)
        assertEquals(16.dp, dimens.bottomSpacing)
        assertEquals(2.dp, dimens.dividerThickness)
    }
}
