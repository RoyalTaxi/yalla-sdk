package uz.yalla.composites.item

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ListItemColorsTest {

    private val colors = ListItemColors(
        container = Color.White,
        title = Color.Black,
        subtitle = Color.Gray,
        disabledContainer = Color.LightGray,
        disabledTitle = Color.DarkGray,
        disabledSubtitle = Color.DarkGray,
    )

    @Test
    fun equality_sameValues_areEqual() {
        val other = ListItemColors(
            container = Color.White,
            title = Color.Black,
            subtitle = Color.Gray,
            disabledContainer = Color.LightGray,
            disabledTitle = Color.DarkGray,
            disabledSubtitle = Color.DarkGray,
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
        val copied = colors.copy(title = Color.Blue)
        assertEquals(Color.Blue, copied.title)
        assertEquals(colors.container, copied.container)
        assertEquals(colors.subtitle, copied.subtitle)
        assertEquals(colors.disabledContainer, copied.disabledContainer)
        assertEquals(colors.disabledTitle, copied.disabledTitle)
        assertEquals(colors.disabledSubtitle, copied.disabledSubtitle)
    }
}

class ListItemDimensTest {

    @Test
    fun defaults_haveExpectedValues() {
        val dimens = ListItemDefaults.dimens()
        assertEquals(RectangleShape, dimens.shape)
        assertEquals(PaddingValues(horizontal = 16.dp, vertical = 12.dp), dimens.contentPadding)
        assertEquals(12.dp, dimens.contentSpacing)
        assertEquals(4.dp, dimens.titleSubtitleSpacing)
        assertEquals(1, dimens.titleMaxLines)
        assertEquals(2, dimens.subtitleMaxLines)
    }

    @Test
    fun dimens_overridesWork() {
        val dimens = ListItemDefaults.dimens(
            contentSpacing = 24.dp,
            titleSubtitleSpacing = 8.dp,
            titleMaxLines = 3,
            subtitleMaxLines = 5,
        )
        assertEquals(24.dp, dimens.contentSpacing)
        assertEquals(8.dp, dimens.titleSubtitleSpacing)
        assertEquals(3, dimens.titleMaxLines)
        assertEquals(5, dimens.subtitleMaxLines)
    }
}
