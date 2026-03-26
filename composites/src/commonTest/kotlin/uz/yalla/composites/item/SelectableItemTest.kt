package uz.yalla.composites.item

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class SelectableItemColorsTest {

    private val colors = SelectableItemColors(
        selectedContainer = Color.LightGray,
        unselectedContainer = Color.Transparent,
        border = Color.Gray,
        text = Color.Black,
    )

    @Test
    fun equality_sameValues_areEqual() {
        val other = SelectableItemColors(
            selectedContainer = Color.LightGray,
            unselectedContainer = Color.Transparent,
            border = Color.Gray,
            text = Color.Black,
        )
        assertEquals(colors, other)
    }

    @Test
    fun equality_differentValues_areNotEqual() {
        val other = colors.copy(selectedContainer = Color.Red)
        assertNotEquals(colors, other)
    }

    @Test
    fun copy_overridesOnlySpecifiedFields() {
        val copied = colors.copy(border = Color.Blue)
        assertEquals(Color.Blue, copied.border)
        assertEquals(colors.selectedContainer, copied.selectedContainer)
        assertEquals(colors.unselectedContainer, copied.unselectedContainer)
        assertEquals(colors.text, copied.text)
    }
}

class SelectableItemDimensTest {

    @Test
    fun defaults_haveExpectedValues() {
        val dimens = SelectableItemDefaults.dimens()
        assertEquals(RoundedCornerShape(16.dp), dimens.shape)
        assertEquals(PaddingValues(horizontal = 16.dp, vertical = 12.dp), dimens.contentPadding)
        assertEquals(12.dp, dimens.iconSpacing)
        assertEquals(1.dp, dimens.borderWidth)
    }

    @Test
    fun dimens_overridesWork() {
        val dimens = SelectableItemDefaults.dimens(
            shape = RoundedCornerShape(8.dp),
            iconSpacing = 16.dp,
            borderWidth = 2.dp,
        )
        assertEquals(RoundedCornerShape(8.dp), dimens.shape)
        assertEquals(16.dp, dimens.iconSpacing)
        assertEquals(2.dp, dimens.borderWidth)
    }
}
