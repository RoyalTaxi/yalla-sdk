package uz.yalla.composites.card

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class AvatarCardColorsTest {

    private val brush = Brush.linearGradient(listOf(Color.Red, Color.Blue))

    private val colors = AvatarCardColors(
        name = Color.Black,
        badgeBackground = brush,
        badgeText = Color.White,
    )

    @Test
    fun equality_sameValues_areEqual() {
        val other = AvatarCardColors(
            name = Color.Black,
            badgeBackground = brush,
            badgeText = Color.White,
        )
        assertEquals(colors, other)
    }

    @Test
    fun equality_differentValues_areNotEqual() {
        val other = colors.copy(name = Color.Red)
        assertNotEquals(colors, other)
    }

    @Test
    fun copy_overridesOnlySpecifiedFields() {
        val copied = colors.copy(badgeText = Color.Blue)
        assertEquals(Color.Blue, copied.badgeText)
        assertEquals(colors.name, copied.name)
        assertEquals(colors.badgeBackground, copied.badgeBackground)
    }
}

class AvatarCardDimensTest {

    @Test
    fun defaults_haveExpectedValues() {
        val dimens = AvatarCardDefaults.dimens()
        assertEquals(80.dp, dimens.avatarSize)
        assertEquals(RoundedCornerShape(8.dp), dimens.badgeShape)
        assertEquals(PaddingValues(horizontal = 8.dp, vertical = 2.dp), dimens.badgePadding)
        assertEquals(12.dp, dimens.nameTopSpacing)
        assertEquals(8.dp, dimens.contentSpacing)
    }

    @Test
    fun dimens_overridesWork() {
        val dimens = AvatarCardDefaults.dimens(
            avatarSize = 100.dp,
            nameTopSpacing = 16.dp,
            contentSpacing = 12.dp,
        )
        assertEquals(100.dp, dimens.avatarSize)
        assertEquals(16.dp, dimens.nameTopSpacing)
        assertEquals(12.dp, dimens.contentSpacing)
    }
}
