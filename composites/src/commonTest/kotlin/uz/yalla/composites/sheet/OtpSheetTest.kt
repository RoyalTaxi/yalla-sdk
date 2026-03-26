package uz.yalla.composites.sheet

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class OtpSheetColorsTest {

    private val colors = OtpSheetColors(
        headline = Color.Black,
        description = Color.Gray,
    )

    @Test
    fun equality_sameValues_areEqual() {
        val other = OtpSheetColors(
            headline = Color.Black,
            description = Color.Gray,
        )
        assertEquals(colors, other)
    }

    @Test
    fun equality_differentValues_areNotEqual() {
        val other = colors.copy(headline = Color.Red)
        assertNotEquals(colors, other)
    }

    @Test
    fun copy_overridesOnlySpecifiedFields() {
        val copied = colors.copy(headline = Color.Blue)
        assertEquals(Color.Blue, copied.headline)
        assertEquals(colors.description, copied.description)
    }
}

class OtpSheetDimensTest {

    @Test
    fun defaults_haveExpectedValues() {
        val dimens = OtpSheetDefaults.dimens()
        assertEquals(10.dp, dimens.headlineDescriptionSpacing)
        assertEquals(32.dp, dimens.descriptionPinSpacing)
    }

    @Test
    fun dimens_overridesWork() {
        val dimens = OtpSheetDefaults.dimens(
            headlineDescriptionSpacing = 16.dp,
            descriptionPinSpacing = 24.dp,
        )
        assertEquals(16.dp, dimens.headlineDescriptionSpacing)
        assertEquals(24.dp, dimens.descriptionPinSpacing)
    }
}
