package uz.yalla.composites.card

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ContentCardColorsTest {

    private val colors = ContentCardColors(
        container = Color.White,
        disabledContainer = Color.LightGray,
    )

    @Test
    fun equality_sameValues_areEqual() {
        val other = ContentCardColors(
            container = Color.White,
            disabledContainer = Color.LightGray,
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
        assertEquals(colors.disabledContainer, copied.disabledContainer)
    }
}

class ContentCardDimensTest {

    @Test
    fun defaults_haveExpectedValues() {
        val dimens = ContentCardDefaults.dimens()
        assertEquals(RoundedCornerShape(16.dp), dimens.shape)
        assertEquals(PaddingValues(16.dp), dimens.contentPadding)
    }

    @Test
    fun dimens_overridesWork() {
        val dimens = ContentCardDefaults.dimens(
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(24.dp),
        )
        assertEquals(RoundedCornerShape(8.dp), dimens.shape)
        assertEquals(PaddingValues(24.dp), dimens.contentPadding)
    }
}
