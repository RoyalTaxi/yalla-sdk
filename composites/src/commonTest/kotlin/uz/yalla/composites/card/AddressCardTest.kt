package uz.yalla.composites.card

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class AddressCardColorsTest {

    private val colors = AddressCardColors(
        container = Color.LightGray,
        title = Color.Black,
        subtitle = Color.Black,
        footer = Color.Gray,
    )

    @Test
    fun equality_sameValues_areEqual() {
        val other = AddressCardColors(
            container = Color.LightGray,
            title = Color.Black,
            subtitle = Color.Black,
            footer = Color.Gray,
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
        val copied = colors.copy(footer = Color.Blue)
        assertEquals(Color.Blue, copied.footer)
        assertEquals(colors.container, copied.container)
        assertEquals(colors.title, copied.title)
        assertEquals(colors.subtitle, copied.subtitle)
    }
}

class AddressCardDimensTest {

    @Test
    fun defaults_haveExpectedValues() {
        val dimens = AddressCardDefaults.dimens()
        assertEquals(RoundedCornerShape(20.dp), dimens.shape)
        assertEquals(248.dp, dimens.maxWidth)
        assertEquals(120.dp, dimens.height)
        assertEquals(PaddingValues(16.dp), dimens.contentPadding)
        assertEquals(8.dp, dimens.titleIconSpacing)
        assertEquals(8.dp, dimens.contentSpacing)
    }

    @Test
    fun dimens_overridesWork() {
        val dimens = AddressCardDefaults.dimens(
            maxWidth = 300.dp,
            height = 160.dp,
            titleIconSpacing = 12.dp,
            contentSpacing = 16.dp,
        )
        assertEquals(300.dp, dimens.maxWidth)
        assertEquals(160.dp, dimens.height)
        assertEquals(12.dp, dimens.titleIconSpacing)
        assertEquals(16.dp, dimens.contentSpacing)
    }
}
