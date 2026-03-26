package uz.yalla.composites.item

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ValueItemColorsTest {

    private val brush = Brush.linearGradient(listOf(Color.Red, Color.Blue))

    private val colors = ValueItemColors(
        background = brush,
        text = Color.White,
    )

    @Test
    fun equality_sameValues_areEqual() {
        val other = ValueItemColors(
            background = brush,
            text = Color.White,
        )
        assertEquals(colors, other)
    }

    @Test
    fun equality_differentValues_areNotEqual() {
        val other = colors.copy(text = Color.Red)
        assertNotEquals(colors, other)
    }

    @Test
    fun copy_overridesOnlySpecifiedFields() {
        val copied = colors.copy(text = Color.Black)
        assertEquals(Color.Black, copied.text)
        assertEquals(colors.background, copied.background)
    }
}

class ValueItemDimensTest {

    @Test
    fun defaults_haveExpectedValues() {
        val dimens = ValueItemDefaults.dimens()
        assertEquals(CircleShape, dimens.shape)
        assertEquals(PaddingValues(4.dp), dimens.contentPadding)
        assertEquals(20.dp, dimens.iconSize)
        assertEquals(4.dp, dimens.iconSpacing)
        assertEquals(12.dp, dimens.trailingSpacing)
    }

    @Test
    fun dimens_overridesWork() {
        val dimens = ValueItemDefaults.dimens(
            iconSize = 32.dp,
            iconSpacing = 8.dp,
            trailingSpacing = 16.dp,
        )
        assertEquals(32.dp, dimens.iconSize)
        assertEquals(8.dp, dimens.iconSpacing)
        assertEquals(16.dp, dimens.trailingSpacing)
    }
}
