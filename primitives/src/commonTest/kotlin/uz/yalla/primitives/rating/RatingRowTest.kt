package uz.yalla.primitives.rating

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class RatingRowColorsTest {

    private val colors = RatingRowColors(
        filled = Color.Yellow,
        empty = Color.Gray,
    )

    @Test
    fun equality_sameValues_areEqual() {
        val other = RatingRowColors(
            filled = Color.Yellow,
            empty = Color.Gray,
        )
        assertEquals(colors, other)
    }

    @Test
    fun equality_differentValues_areNotEqual() {
        val other = colors.copy(filled = Color.Red)
        assertNotEquals(colors, other)
    }

    @Test
    fun copy_overridesOnlySpecifiedFields() {
        val copied = colors.copy(empty = Color.LightGray)
        assertEquals(Color.LightGray, copied.empty)
        assertEquals(colors.filled, copied.filled)
    }
}

class RatingRowDimensTest {

    @Test
    fun defaults_haveExpectedValues() {
        val dimens = RatingRowDefaults.dimens()
        assertEquals(50.dp, dimens.starSize)
        assertEquals(10.dp, dimens.starPadding)
        assertEquals(6.dp, dimens.starSpacing)
        assertEquals(5, dimens.starCount)
    }

    @Test
    fun dimens_overridesWork() {
        val dimens = RatingRowDefaults.dimens(
            starSize = 32.dp,
            starPadding = 4.dp,
            starSpacing = 8.dp,
            starCount = 10,
        )
        assertEquals(32.dp, dimens.starSize)
        assertEquals(4.dp, dimens.starPadding)
        assertEquals(8.dp, dimens.starSpacing)
        assertEquals(10, dimens.starCount)
    }
}
