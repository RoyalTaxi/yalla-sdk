package uz.yalla.composites.card

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ToggleCardColorsTest {

    private val colors = ToggleCardColors(
        container = Color.Transparent,
        iconBackground = Color.LightGray,
    )

    @Test
    fun equality_sameValues_areEqual() {
        val other = ToggleCardColors(container = Color.Transparent, iconBackground = Color.LightGray)
        assertEquals(colors, other)
    }

    @Test
    fun equality_differentValues_areNotEqual() {
        assertNotEquals(colors, colors.copy(iconBackground = Color.Red))
    }
}

class ToggleCardDimensTest {

    @Test
    fun defaults_haveExpectedValues() {
        val dimens = ToggleCardDefaults.dimens()
        assertEquals(PaddingValues(horizontal = 16.dp, vertical = 10.dp), dimens.contentPadding)
        assertEquals(44.dp, dimens.iconSize)
        assertEquals(RoundedCornerShape(10.dp), dimens.iconShape)
        assertEquals(10.dp, dimens.iconPadding)
        assertEquals(16.dp, dimens.iconSpacing)
        assertEquals(28.dp, dimens.trailingSpacing)
    }
}
