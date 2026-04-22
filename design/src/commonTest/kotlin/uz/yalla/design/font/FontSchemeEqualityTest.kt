package uz.yalla.design.font

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class FontSchemeEqualityTest {

    private fun sample(): FontScheme = FontScheme(
        title = FontScheme.Title(
            xLarge = TextStyle(fontSize = 30.sp),
            large = TextStyle(fontSize = 22.sp),
            base = TextStyle(fontSize = 20.sp),
        ),
        body = FontScheme.Body(
            caption = TextStyle(fontSize = 13.sp),
            large = FontScheme.Body.Weighty(
                regular = TextStyle(fontSize = 18.sp),
                medium = TextStyle(fontSize = 18.sp),
                bold = TextStyle(fontSize = 18.sp),
            ),
            base = FontScheme.Body.Weighty(
                regular = TextStyle(fontSize = 16.sp),
                medium = TextStyle(fontSize = 16.sp),
                bold = TextStyle(fontSize = 16.sp),
            ),
            small = FontScheme.Body.Weighty(
                regular = TextStyle(fontSize = 14.sp),
                medium = TextStyle(fontSize = 14.sp),
                bold = TextStyle(fontSize = 14.sp),
            ),
        ),
        custom = FontScheme.Custom(
            carNumber = TextStyle(fontSize = 12.sp),
        ),
    )

    @Test
    fun structuralEquality_sameContent_areEqual() {
        assertEquals(sample(), sample())
        assertEquals(sample().hashCode(), sample().hashCode())
    }

    @Test
    fun structuralEquality_differentTitle_areNotEqual() {
        val a = sample()
        val b = sample().copy(
            title = a.title.copy(large = TextStyle(fontSize = 99.sp)),
        )
        assertNotEquals(a, b)
    }

    @Test
    fun structuralEquality_differentCustom_areNotEqual() {
        val a = sample()
        val b = sample().copy(
            custom = FontScheme.Custom(carNumber = TextStyle(fontSize = 42.sp)),
        )
        assertNotEquals(a, b)
    }
}
