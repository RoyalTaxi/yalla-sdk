package uz.yalla.composites.card

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class FeedCardColorsTest {

    private val colors = FeedCardColors(
        container = Color.White,
        border = Color.Gray,
        indicator = Color.Blue,
    )

    @Test
    fun equality_sameValues_areEqual() {
        val other = FeedCardColors(container = Color.White, border = Color.Gray, indicator = Color.Blue)
        assertEquals(colors, other)
    }

    @Test
    fun equality_differentValues_areNotEqual() {
        assertNotEquals(colors, colors.copy(indicator = Color.Red))
    }

    @Test
    fun copy_overridesOnlySpecifiedFields() {
        val copied = colors.copy(border = Color.Black)
        assertEquals(Color.Black, copied.border)
        assertEquals(colors.container, copied.container)
        assertEquals(colors.indicator, copied.indicator)
    }
}

class FeedCardDimensTest {

    @Test
    fun defaults_haveExpectedValues() {
        val dimens = FeedCardDefaults.dimens()
        assertEquals(RoundedCornerShape(8.dp), dimens.shape)
        assertEquals(1.dp, dimens.borderWidth)
        assertEquals(PaddingValues(horizontal = 16.dp, vertical = 12.dp), dimens.contentPadding)
        assertEquals(64.dp, dimens.indicatorHeight)
        assertEquals(4.dp, dimens.indicatorWidth)
        assertEquals(2.dp, dimens.indicatorRadius)
    }

    @Test
    fun dimens_overridesWork() {
        val dimens = FeedCardDefaults.dimens(indicatorHeight = 48.dp, indicatorWidth = 6.dp)
        assertEquals(48.dp, dimens.indicatorHeight)
        assertEquals(6.dp, dimens.indicatorWidth)
    }
}
