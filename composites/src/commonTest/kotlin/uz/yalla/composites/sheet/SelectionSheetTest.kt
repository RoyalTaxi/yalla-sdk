package uz.yalla.composites.sheet

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

class SelectionSheetDimensTest {

    @Test
    fun defaults_haveExpectedValues() {
        val dimens = SelectionSheetDefaults.dimens()
        assertEquals(RoundedCornerShape(topStart = 38.dp, topEnd = 38.dp), dimens.shape)
        assertEquals(PaddingValues(10.dp), dimens.contentPadding)
        assertEquals(24.dp, dimens.headerContentSpacing)
        assertEquals(10.dp, dimens.itemSpacing)
    }

    @Test
    fun dimens_overridesWork() {
        val dimens = SelectionSheetDefaults.dimens(
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(24.dp),
            headerContentSpacing = 32.dp,
            itemSpacing = 16.dp,
        )
        assertEquals(RoundedCornerShape(16.dp), dimens.shape)
        assertEquals(PaddingValues(24.dp), dimens.contentPadding)
        assertEquals(32.dp, dimens.headerContentSpacing)
        assertEquals(16.dp, dimens.itemSpacing)
    }
}
