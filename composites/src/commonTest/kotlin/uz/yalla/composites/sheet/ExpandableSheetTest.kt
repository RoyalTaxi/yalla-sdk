package uz.yalla.composites.sheet

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ExpandableSheetColorsTest {

    private val colors = ExpandableSheetColors(
        container = Color.White,
    )

    @Test
    fun equality_sameValues_areEqual() {
        val other = ExpandableSheetColors(
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

class ExpandableSheetDimensTest {

    @Test
    fun defaults_haveExpectedValues() {
        val dimens = ExpandableSheetDefaults.dimens()
        assertEquals(38.dp, dimens.cornerRadius)
        assertEquals(RoundedCornerShape(topStart = 38.dp, topEnd = 38.dp), dimens.shape)
    }

    @Test
    fun dimens_overridesWork() {
        val dimens = ExpandableSheetDefaults.dimens(
            cornerRadius = 16.dp,
        )
        assertEquals(16.dp, dimens.cornerRadius)
        assertEquals(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp), dimens.shape)
    }
}
