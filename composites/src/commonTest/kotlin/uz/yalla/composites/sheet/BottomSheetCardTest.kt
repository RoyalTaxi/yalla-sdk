package uz.yalla.composites.sheet

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class BottomSheetCardColorsTest {

    private val colors = BottomSheetCardColors(
        container = Color.White,
    )

    @Test
    fun equality_sameValues_areEqual() {
        val other = BottomSheetCardColors(
            container = Color.White,
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
        val copied = colors.copy(container = Color.Blue)
        assertEquals(Color.Blue, copied.container)
    }
}

class BottomSheetCardDimensTest {

    @Test
    fun defaults_haveExpectedValues() {
        val dimens = BottomSheetCardDefaults.dimens()
        assertEquals(38.dp, dimens.cornerRadius)
        assertEquals(RoundedCornerShape(topStart = 38.dp, topEnd = 38.dp), dimens.shape)
    }

    @Test
    fun dimens_overridesWork() {
        val dimens = BottomSheetCardDefaults.dimens(
            cornerRadius = 16.dp,
        )
        assertEquals(16.dp, dimens.cornerRadius)
        assertEquals(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp), dimens.shape)
    }
}

class BottomSheetCardAnimationTest {

    @Test
    fun defaults_haveExpectedValues() {
        val animation = BottomSheetCardDefaults.animation()
        assertEquals(250, animation.durationMillis)
        assertEquals(0.65f, animation.collapsedFraction)
    }

    @Test
    fun animation_overridesWork() {
        val animation = BottomSheetCardDefaults.animation(
            durationMillis = 500,
            collapsedFraction = 0.5f,
        )
        assertEquals(500, animation.durationMillis)
        assertEquals(0.5f, animation.collapsedFraction)
    }
}
