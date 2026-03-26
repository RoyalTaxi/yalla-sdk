package uz.yalla.composites.card

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class NavigableCardColorsTest {

    private val colors = NavigableCardColors(
        container = Color.Transparent,
        border = Color.Gray,
        arrow = Color.Black,
        disabledContainer = Color.Transparent,
        disabledBorder = Color.LightGray,
        disabledArrow = Color.LightGray,
    )

    @Test
    fun equality_sameValues_areEqual() {
        val other = NavigableCardColors(
            container = Color.Transparent,
            border = Color.Gray,
            arrow = Color.Black,
            disabledContainer = Color.Transparent,
            disabledBorder = Color.LightGray,
            disabledArrow = Color.LightGray,
        )
        assertEquals(colors, other)
    }

    @Test
    fun equality_differentValues_areNotEqual() {
        assertNotEquals(colors, colors.copy(border = Color.Red))
    }

    @Test
    fun copy_overridesOnlySpecifiedFields() {
        val copied = colors.copy(arrow = Color.Blue)
        assertEquals(Color.Blue, copied.arrow)
        assertEquals(colors.border, copied.border)
    }
}

class NavigableCardDimensTest {

    @Test
    fun defaults_haveExpectedValues() {
        val dimens = NavigableCardDefaults.dimens()
        assertEquals(RoundedCornerShape(16.dp), dimens.shape)
        assertEquals(1.dp, dimens.borderWidth)
        assertEquals(24.dp, dimens.arrowSize)
        assertEquals(8.dp, dimens.iconSpacing)
        assertEquals(PaddingValues(horizontal = 16.dp, vertical = 18.dp), dimens.contentPadding)
    }

    @Test
    fun dimens_overridesWork() {
        val dimens = NavigableCardDefaults.dimens(arrowSize = 16.dp, iconSpacing = 12.dp)
        assertEquals(16.dp, dimens.arrowSize)
        assertEquals(12.dp, dimens.iconSpacing)
    }
}
