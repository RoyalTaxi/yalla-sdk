package uz.yalla.composites.item

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class PricingItemColorsTest {

    private val brush = Brush.linearGradient(listOf(Color.Red, Color.Blue))

    private val colors = PricingItemColors(
        container = Color.LightGray,
        selectedContainer = Color.White,
        name = Color.Black,
        price = Color.Black,
        selectedBorder = brush,
    )

    @Test
    fun equality_sameValues_areEqual() {
        val other = PricingItemColors(
            container = Color.LightGray,
            selectedContainer = Color.White,
            name = Color.Black,
            price = Color.Black,
            selectedBorder = brush,
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
        val copied = colors.copy(name = Color.Blue)
        assertEquals(Color.Blue, copied.name)
        assertEquals(colors.container, copied.container)
        assertEquals(colors.selectedContainer, copied.selectedContainer)
        assertEquals(colors.price, copied.price)
        assertEquals(colors.selectedBorder, copied.selectedBorder)
    }
}

class PricingItemDimensTest {

    @Test
    fun defaults_haveExpectedValues() {
        val dimens = PricingItemDefaults.dimens()
        assertEquals(RoundedCornerShape(20.dp), dimens.shape)
        assertEquals(120.dp, dimens.height)
        assertEquals(140.dp, dimens.minWidth)
        assertEquals(12.dp, dimens.contentPadding)
        assertEquals(2.dp, dimens.selectedBorderWidth)
        assertEquals(6.dp, dimens.namePriceSpacing)
        assertEquals(10.dp, dimens.priceImageSpacing)
        assertEquals(1, dimens.textMaxLines)
    }

    @Test
    fun dimens_overridesWork() {
        val dimens = PricingItemDefaults.dimens(
            height = 160.dp,
            minWidth = 200.dp,
            contentPadding = 16.dp,
            textMaxLines = 2,
        )
        assertEquals(160.dp, dimens.height)
        assertEquals(200.dp, dimens.minWidth)
        assertEquals(16.dp, dimens.contentPadding)
        assertEquals(2, dimens.textMaxLines)
    }
}
