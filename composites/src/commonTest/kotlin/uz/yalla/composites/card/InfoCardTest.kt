package uz.yalla.composites.card

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class InfoCardColorsTest {

    private val colors = InfoCardColors(container = Color.White)

    @Test
    fun equality_sameValues_areEqual() {
        assertEquals(colors, InfoCardColors(container = Color.White))
    }

    @Test
    fun equality_differentValues_areNotEqual() {
        assertNotEquals(colors, InfoCardColors(container = Color.Red))
    }
}

class InfoCardDimensTest {

    @Test
    fun defaults_haveExpectedValues() {
        val dimens = InfoCardDefaults.dimens()
        assertEquals(RoundedCornerShape(20.dp), dimens.shape)
        assertEquals(120.dp, dimens.height)
        assertEquals(
            PaddingValues(top = 10.dp, end = 10.dp, bottom = 8.dp, start = 16.dp),
            dimens.contentPadding,
        )
    }

    @Test
    fun dimens_overridesWork() {
        val dimens = InfoCardDefaults.dimens(height = 160.dp)
        assertEquals(160.dp, dimens.height)
    }
}
