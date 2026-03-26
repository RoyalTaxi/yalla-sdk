package uz.yalla.composites.item

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class NavigableItemColorsTest {

    private val colors = NavigableItemColors(
        container = Color.White,
        iconBackground = Color.LightGray,
        icon = Color.Black,
        title = Color.Black,
        subtitle = Color.Gray,
        arrow = Color.DarkGray,
    )

    @Test
    fun equality_sameValues_areEqual() {
        val other = NavigableItemColors(
            container = Color.White,
            iconBackground = Color.LightGray,
            icon = Color.Black,
            title = Color.Black,
            subtitle = Color.Gray,
            arrow = Color.DarkGray,
        )
        assertEquals(colors, other)
    }

    @Test
    fun equality_differentValues_areNotEqual() {
        val other = colors.copy(arrow = Color.Red)
        assertNotEquals(colors, other)
    }

    @Test
    fun copy_overridesOnlySpecifiedFields() {
        val copied = colors.copy(icon = Color.Blue)
        assertEquals(Color.Blue, copied.icon)
        assertEquals(colors.container, copied.container)
        assertEquals(colors.iconBackground, copied.iconBackground)
        assertEquals(colors.title, copied.title)
        assertEquals(colors.subtitle, copied.subtitle)
        assertEquals(colors.arrow, copied.arrow)
    }
}

class NavigableItemDimensTest {

    @Test
    fun defaults_haveExpectedValues() {
        val dimens = NavigableItemDefaults.dimens()
        assertEquals(60.dp, dimens.height)
        assertEquals(PaddingValues(horizontal = 10.dp, vertical = 0.dp), dimens.contentPadding)
        assertEquals(16.dp, dimens.contentSpacing)
        assertEquals(44.dp, dimens.iconContainerSize)
        assertEquals(RoundedCornerShape(10.dp), dimens.iconContainerShape)
        assertEquals(10.dp, dimens.iconPadding)
        assertEquals(16.dp, dimens.arrowSize)
    }

    @Test
    fun dimens_overridesWork() {
        val dimens = NavigableItemDefaults.dimens(
            height = 80.dp,
            contentSpacing = 24.dp,
            iconContainerSize = 56.dp,
            arrowSize = 20.dp,
        )
        assertEquals(80.dp, dimens.height)
        assertEquals(24.dp, dimens.contentSpacing)
        assertEquals(56.dp, dimens.iconContainerSize)
        assertEquals(20.dp, dimens.arrowSize)
    }
}
