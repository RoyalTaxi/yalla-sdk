package uz.yalla.composites.item

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class IconItemColorsTest {

    private val colors = IconItemColors(
        container = Color.White,
        iconBackground = Color.LightGray,
        title = Color.Black,
        subtitle = Color.Gray,
    )

    @Test
    fun equality_sameValues_areEqual() {
        val other = IconItemColors(
            container = Color.White,
            iconBackground = Color.LightGray,
            title = Color.Black,
            subtitle = Color.Gray,
        )
        assertEquals(colors, other)
    }

    @Test
    fun equality_differentValues_areNotEqual() {
        val other = colors.copy(iconBackground = Color.Red)
        assertNotEquals(colors, other)
    }

    @Test
    fun copy_overridesOnlySpecifiedFields() {
        val copied = colors.copy(title = Color.Blue)
        assertEquals(Color.Blue, copied.title)
        assertEquals(colors.container, copied.container)
        assertEquals(colors.iconBackground, copied.iconBackground)
        assertEquals(colors.subtitle, copied.subtitle)
    }
}

class IconItemDimensTest {

    @Test
    fun defaults_haveExpectedValues() {
        val dimens = IconItemDefaults.dimens()
        assertEquals(RectangleShape, dimens.shape)
        assertEquals(PaddingValues(horizontal = 20.dp, vertical = 8.dp), dimens.contentPadding)
        assertEquals(16.dp, dimens.contentSpacing)
        assertEquals(24.dp, dimens.iconContainerSize)
        assertEquals(RoundedCornerShape(6.dp), dimens.iconContainerShape)
        assertEquals(0.dp, dimens.iconPadding)
        assertEquals(4.dp, dimens.titleSubtitleSpacing)
    }

    @Test
    fun dimens_overridesWork() {
        val dimens = IconItemDefaults.dimens(
            contentSpacing = 24.dp,
            iconContainerSize = 48.dp,
            iconPadding = 8.dp,
        )
        assertEquals(24.dp, dimens.contentSpacing)
        assertEquals(48.dp, dimens.iconContainerSize)
        assertEquals(8.dp, dimens.iconPadding)
    }
}
