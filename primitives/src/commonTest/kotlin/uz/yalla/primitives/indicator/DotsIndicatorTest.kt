package uz.yalla.primitives.indicator

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class DotsIndicatorColorsTest {

    private val colors = DotsIndicatorColors(
        selected = Color.Blue,
        unselected = Color.LightGray,
    )

    @Test
    fun equality_sameValues_areEqual() {
        val other = DotsIndicatorColors(
            selected = Color.Blue,
            unselected = Color.LightGray,
        )
        assertEquals(colors, other)
    }

    @Test
    fun equality_differentValues_areNotEqual() {
        val other = colors.copy(selected = Color.Red)
        assertNotEquals(colors, other)
    }

    @Test
    fun copy_overridesOnlySpecifiedFields() {
        val copied = colors.copy(selected = Color.Green)
        assertEquals(Color.Green, copied.selected)
        assertEquals(colors.unselected, copied.unselected)
    }
}

class DotsIndicatorDimensTest {

    @Test
    fun defaults_haveExpectedValues() {
        val dimens = DotsIndicatorDefaults.dimens()
        assertEquals(10.dp, dimens.dotSize)
        assertEquals(24.dp, dimens.selectedWidth)
        assertEquals(4.dp, dimens.dotSpacing)
        assertEquals(200, dimens.animationDurationMillis)
    }

    @Test
    fun dimens_overridesWork() {
        val dimens = DotsIndicatorDefaults.dimens(
            dotSize = 8.dp,
            selectedWidth = 32.dp,
            dotSpacing = 6.dp,
            animationDurationMillis = 300,
        )
        assertEquals(8.dp, dimens.dotSize)
        assertEquals(32.dp, dimens.selectedWidth)
        assertEquals(6.dp, dimens.dotSpacing)
        assertEquals(300, dimens.animationDurationMillis)
    }
}
