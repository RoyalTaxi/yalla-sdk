package uz.yalla.composites.card

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class SummaryCardColorsTest {

    private val colors = SummaryCardColors(container = Color.White)

    @Test
    fun equality_sameValues_areEqual() {
        assertEquals(colors, SummaryCardColors(container = Color.White))
    }

    @Test
    fun equality_differentValues_areNotEqual() {
        assertNotEquals(colors, SummaryCardColors(container = Color.Red))
    }
}

class SummaryCardDimensTest {

    @Test
    fun defaults_haveExpectedValues() {
        val dimens = SummaryCardDefaults.dimens()
        assertEquals(RoundedCornerShape(16.dp), dimens.shape)
        assertEquals(PaddingValues(16.dp), dimens.contentPadding)
        assertEquals(8.dp, dimens.headerSpacing)
        assertEquals(16.dp, dimens.trailingSpacing)
    }

    @Test
    fun dimens_overridesWork() {
        val dimens = SummaryCardDefaults.dimens(headerSpacing = 12.dp, trailingSpacing = 24.dp)
        assertEquals(12.dp, dimens.headerSpacing)
        assertEquals(24.dp, dimens.trailingSpacing)
    }
}
