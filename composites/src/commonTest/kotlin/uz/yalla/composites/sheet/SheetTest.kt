package uz.yalla.composites.sheet

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class SheetColorsTest {

    private val colors = SheetColors(
        container = Color.White,
        scrim = Color.Black,
    )

    @Test
    fun equality_sameValues_areEqual() {
        val other = SheetColors(
            container = Color.White,
            scrim = Color.Black,
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
        assertEquals(colors.scrim, copied.scrim)
    }
}

class SheetDimensTest {

    @Test
    fun defaults_haveExpectedValues() {
        val dimens = SheetDefaults.dimens()
        assertEquals(RoundedCornerShape(topStart = 38.dp, topEnd = 38.dp), dimens.shape)
        assertEquals(Dp.Unspecified, dimens.maxWidth)
        assertEquals(36.dp, dimens.dragHandleWidth)
        assertEquals(5.dp, dimens.dragHandleHeight)
        assertEquals(36.dp, dimens.dragHandleContainerWidth)
        assertEquals(16.dp, dimens.dragHandleContainerHeight)
    }

    @Test
    fun dimens_overridesWork() {
        val dimens = SheetDefaults.dimens(
            shape = RoundedCornerShape(16.dp),
            maxWidth = 400.dp,
            dragHandleWidth = 48.dp,
            dragHandleHeight = 8.dp,
            dragHandleContainerWidth = 48.dp,
            dragHandleContainerHeight = 24.dp,
        )
        assertEquals(RoundedCornerShape(16.dp), dimens.shape)
        assertEquals(400.dp, dimens.maxWidth)
        assertEquals(48.dp, dimens.dragHandleWidth)
        assertEquals(8.dp, dimens.dragHandleHeight)
        assertEquals(48.dp, dimens.dragHandleContainerWidth)
        assertEquals(24.dp, dimens.dragHandleContainerHeight)
    }
}
