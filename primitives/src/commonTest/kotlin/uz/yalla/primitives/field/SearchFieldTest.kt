package uz.yalla.primitives.field

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class SearchFieldColorsTest {

    private val colors = SearchFieldColors(
        container = Color.LightGray,
        text = Color.Black,
        placeholder = Color.Gray,
    )

    @Test
    fun equality_sameValues_areEqual() {
        val other = SearchFieldColors(
            container = Color.LightGray,
            text = Color.Black,
            placeholder = Color.Gray,
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
        val copied = colors.copy(placeholder = Color.Blue)
        assertEquals(Color.Blue, copied.placeholder)
        assertEquals(colors.container, copied.container)
        assertEquals(colors.text, copied.text)
    }
}

class SearchFieldDimensTest {

    @Test
    fun defaults_haveExpectedValues() {
        val dimens = SearchFieldDefaults.dimens()
        assertEquals(RoundedCornerShape(16.dp), dimens.shape)
        assertEquals(PaddingValues(horizontal = 16.dp, vertical = 12.dp), dimens.contentPadding)
        assertEquals(8.dp, dimens.iconSpacing)
        assertEquals(48.dp, dimens.minHeight)
    }

    @Test
    fun dimens_overridesWork() {
        val dimens = SearchFieldDefaults.dimens(
            shape = RoundedCornerShape(8.dp),
            iconSpacing = 12.dp,
            minHeight = 56.dp,
        )
        assertEquals(RoundedCornerShape(8.dp), dimens.shape)
        assertEquals(12.dp, dimens.iconSpacing)
        assertEquals(56.dp, dimens.minHeight)
    }
}
