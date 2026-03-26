package uz.yalla.composites.sheet

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class SheetHeaderColorsTest {

    private val colors = SheetHeaderColors(
        title = Color.Black,
    )

    @Test
    fun equality_sameValues_areEqual() {
        val other = SheetHeaderColors(
            title = Color.Black,
        )
        assertEquals(colors, other)
    }

    @Test
    fun equality_differentValues_areNotEqual() {
        val other = colors.copy(title = Color.Red)
        assertNotEquals(colors, other)
    }

    @Test
    fun copy_overridesOnlySpecifiedFields() {
        val copied = colors.copy(title = Color.Blue)
        assertEquals(Color.Blue, copied.title)
    }
}

class SheetHeaderDimensTest {

    @Test
    fun defaults_haveExpectedValues() {
        val dimens = SheetHeaderDefaults.dimens()
        assertEquals(PaddingValues(10.dp), dimens.contentPadding)
    }

    @Test
    fun dimens_overridesWork() {
        val dimens = SheetHeaderDefaults.dimens(
            contentPadding = PaddingValues(24.dp),
        )
        assertEquals(PaddingValues(24.dp), dimens.contentPadding)
    }
}
