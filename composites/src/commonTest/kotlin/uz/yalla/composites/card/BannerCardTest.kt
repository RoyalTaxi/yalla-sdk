package uz.yalla.composites.card

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class BannerCardColorsTest {

    private val colors = BannerCardColors(contentColor = Color.White)

    @Test
    fun equality_sameValues_areEqual() {
        assertEquals(colors, BannerCardColors(contentColor = Color.White))
    }

    @Test
    fun equality_differentValues_areNotEqual() {
        assertNotEquals(colors, BannerCardColors(contentColor = Color.Black))
    }
}

class BannerCardDimensTest {

    @Test
    fun defaults_haveExpectedValues() {
        val dimens = BannerCardDefaults.dimens()
        assertEquals(RoundedCornerShape(16.dp), dimens.shape)
        assertEquals(148.dp, dimens.height)
        assertEquals(PaddingValues(vertical = 16.dp, horizontal = 20.dp), dimens.contentPadding)
    }

    @Test
    fun dimens_overridesWork() {
        val dimens = BannerCardDefaults.dimens(height = 200.dp)
        assertEquals(200.dp, dimens.height)
    }
}
