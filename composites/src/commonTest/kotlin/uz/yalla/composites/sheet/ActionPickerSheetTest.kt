package uz.yalla.composites.sheet

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ActionPickerColorsTest {

    private val colors = ActionPickerColors(
        container = Color.White,
        title = Color.Black,
        itemBackground = Color.LightGray,
        itemIcon = Color.DarkGray,
        itemText = Color.Black,
        destructiveItemIcon = Color.Red,
    )

    @Test
    fun equality_sameValues_areEqual() {
        val other = ActionPickerColors(
            container = Color.White,
            title = Color.Black,
            itemBackground = Color.LightGray,
            itemIcon = Color.DarkGray,
            itemText = Color.Black,
            destructiveItemIcon = Color.Red,
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
        assertEquals(colors.itemBackground, copied.itemBackground)
        assertEquals(colors.itemIcon, copied.itemIcon)
        assertEquals(colors.itemText, copied.itemText)
        assertEquals(colors.destructiveItemIcon, copied.destructiveItemIcon)
    }
}

class ActionPickerDimensTest {

    @Test
    fun defaults_haveExpectedValues() {
        val dimens = ActionPickerDefaults.dimens()
        assertEquals(RoundedCornerShape(topStart = 38.dp, topEnd = 38.dp), dimens.shape)
        assertEquals(PaddingValues(10.dp), dimens.contentPadding)
        assertEquals(24.dp, dimens.titleItemsSpacing)
        assertEquals(10.dp, dimens.itemSpacing)
        assertEquals(RoundedCornerShape(16.dp), dimens.itemShape)
        assertEquals(PaddingValues(18.dp), dimens.itemPadding)
        assertEquals(12.dp, dimens.itemIconTextSpacing)
    }

    @Test
    fun dimens_overridesWork() {
        val dimens = ActionPickerDefaults.dimens(
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(24.dp),
            titleItemsSpacing = 32.dp,
            itemSpacing = 16.dp,
            itemShape = RoundedCornerShape(8.dp),
            itemPadding = PaddingValues(12.dp),
            itemIconTextSpacing = 8.dp,
        )
        assertEquals(RoundedCornerShape(16.dp), dimens.shape)
        assertEquals(PaddingValues(24.dp), dimens.contentPadding)
        assertEquals(32.dp, dimens.titleItemsSpacing)
        assertEquals(16.dp, dimens.itemSpacing)
        assertEquals(RoundedCornerShape(8.dp), dimens.itemShape)
        assertEquals(PaddingValues(12.dp), dimens.itemPadding)
        assertEquals(8.dp, dimens.itemIconTextSpacing)
    }
}
