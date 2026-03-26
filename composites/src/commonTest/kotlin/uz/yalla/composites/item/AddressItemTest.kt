package uz.yalla.composites.item

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class AddressItemColorsTest {

    private val colors = AddressItemColors(
        container = Color.LightGray,
        placeholder = Color.Gray,
        location = Color.Black,
        arrow = Color.DarkGray,
    )

    @Test
    fun equality_sameValues_areEqual() {
        val other = AddressItemColors(
            container = Color.LightGray,
            placeholder = Color.Gray,
            location = Color.Black,
            arrow = Color.DarkGray,
        )
        assertEquals(colors, other)
    }

    @Test
    fun equality_differentValues_areNotEqual() {
        val other = colors.copy(location = Color.Red)
        assertNotEquals(colors, other)
    }

    @Test
    fun copy_overridesOnlySpecifiedFields() {
        val copied = colors.copy(container = Color.Blue)
        assertEquals(Color.Blue, copied.container)
        assertEquals(colors.placeholder, copied.placeholder)
        assertEquals(colors.location, copied.location)
        assertEquals(colors.arrow, copied.arrow)
    }
}

class AddressItemDimensTest {

    @Test
    fun defaults_haveExpectedValues() {
        val dimens = AddressItemDefaults.dimens()
        assertEquals(RoundedCornerShape(16.dp), dimens.shape)
        assertEquals(60.dp, dimens.minHeight)
        assertEquals(12.dp, dimens.contentSpacing)
        assertEquals(6.dp, dimens.flowRowSpacing)
        assertEquals(14.dp, dimens.dotSize)
        assertEquals(4.dp, dimens.dotBorderWidth)
        assertEquals(16.dp, dimens.horizontalPadding)
        assertEquals(1, dimens.locationMaxLines)
    }

    @Test
    fun dimens_overridesWork() {
        val dimens = AddressItemDefaults.dimens(
            minHeight = 80.dp,
            contentSpacing = 16.dp,
            dotSize = 18.dp,
            locationMaxLines = 2,
        )
        assertEquals(80.dp, dimens.minHeight)
        assertEquals(16.dp, dimens.contentSpacing)
        assertEquals(18.dp, dimens.dotSize)
        assertEquals(2, dimens.locationMaxLines)
    }
}
