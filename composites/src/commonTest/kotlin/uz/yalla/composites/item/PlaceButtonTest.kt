package uz.yalla.composites.item

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class PlaceButtonColorsTest {

    private val colors = PlaceButtonColors(
        container = Color.LightGray,
        text = Color.Black,
    )

    @Test
    fun equality_sameValues_areEqual() {
        val other = PlaceButtonColors(
            container = Color.LightGray,
            text = Color.Black,
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
        val copied = colors.copy(container = Color.Blue)
        assertEquals(Color.Blue, copied.container)
        assertEquals(colors.text, copied.text)
    }
}

class PlaceButtonDimensTest {

    @Test
    fun defaults_haveExpectedValues() {
        val dimens = PlaceButtonDefaults.dimens()
        assertEquals(RoundedCornerShape(16.dp), dimens.shape)
        assertEquals(PaddingValues(0.dp), dimens.contentPadding)
        assertEquals(12.dp, dimens.iconSpacing)
    }

    @Test
    fun dimens_overridesWork() {
        val dimens = PlaceButtonDefaults.dimens(
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(16.dp),
            iconSpacing = 24.dp,
        )
        assertEquals(RoundedCornerShape(8.dp), dimens.shape)
        assertEquals(PaddingValues(16.dp), dimens.contentPadding)
        assertEquals(24.dp, dimens.iconSpacing)
    }
}
