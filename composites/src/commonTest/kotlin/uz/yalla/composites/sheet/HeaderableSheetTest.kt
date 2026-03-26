package uz.yalla.composites.sheet

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class HeaderableSheetColorsTest {

    private val colors = HeaderableSheetColors(
        container = Color.White,
        dragHandle = Color.Gray,
    )

    @Test
    fun equality_sameValues_areEqual() {
        val other = HeaderableSheetColors(
            container = Color.White,
            dragHandle = Color.Gray,
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
        assertEquals(colors.dragHandle, copied.dragHandle)
    }
}

class HeaderableSheetDimensTest {

    @Test
    fun defaults_haveExpectedValues() {
        val dimens = HeaderableSheetDefaults.dimens()
        assertEquals(38.dp, dimens.cornerRadius)
        assertEquals(RoundedCornerShape(topStart = 38.dp, topEnd = 38.dp), dimens.shape)
        assertEquals(36.dp, dimens.dragHandleWidth)
        assertEquals(5.dp, dimens.dragHandleHeight)
        assertEquals(16.dp, dimens.dragHandleContainerHeight)
    }

    @Test
    fun dimens_overridesWork() {
        val dimens = HeaderableSheetDefaults.dimens(
            cornerRadius = 16.dp,
            dragHandleWidth = 48.dp,
            dragHandleHeight = 8.dp,
            dragHandleContainerHeight = 24.dp,
        )
        assertEquals(16.dp, dimens.cornerRadius)
        assertEquals(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp), dimens.shape)
        assertEquals(48.dp, dimens.dragHandleWidth)
        assertEquals(8.dp, dimens.dragHandleHeight)
        assertEquals(24.dp, dimens.dragHandleContainerHeight)
    }
}
