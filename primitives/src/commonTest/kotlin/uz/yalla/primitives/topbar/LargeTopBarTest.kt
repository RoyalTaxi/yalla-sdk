package uz.yalla.primitives.topbar

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class LargeTopBarColorsTest {

    private val colors = LargeTopBarColors(
        container = Color.White,
        title = Color.Black,
    )

    @Test
    fun equality_sameValues_areEqual() {
        val other = LargeTopBarColors(
            container = Color.White,
            title = Color.Black,
        )
        assertEquals(colors, other)
    }

    @Test
    fun equality_differentContainer_areNotEqual() {
        val other = colors.copy(container = Color.Red)
        assertNotEquals(colors, other)
    }

    @Test
    fun equality_differentTitle_areNotEqual() {
        val other = colors.copy(title = Color.Blue)
        assertNotEquals(colors, other)
    }

    @Test
    fun copy_overridesOnlySpecifiedFields() {
        val copied = colors.copy(title = Color.Blue)
        assertEquals(Color.Blue, copied.title)
        assertEquals(colors.container, copied.container)
    }

    @Test
    fun copy_withNoArgs_returnsEqualInstance() {
        assertEquals(colors, colors.copy())
    }
}

class LargeTopBarDimensTest {

    @Test
    fun defaults_areNonNull() {
        val dimens = LargeTopBarDefaults.dimens()
        assertNotNull(dimens)
    }

    @Test
    fun defaults_navigationButtonSize_is40dp() {
        val dimens = LargeTopBarDefaults.dimens()
        assertEquals(40.dp, dimens.navigationButtonSize)
    }

    @Test
    fun defaults_contentPadding_is16dp() {
        val dimens = LargeTopBarDefaults.dimens()
        assertEquals(PaddingValues(16.dp), dimens.contentPadding)
    }

    @Test
    fun defaults_titleTopSpacing_is20dp() {
        val dimens = LargeTopBarDefaults.dimens()
        assertEquals(20.dp, dimens.titleTopSpacing)
    }

    @Test
    fun dimenEquality_sameValues_areEqual() {
        val a = LargeTopBarDefaults.dimens()
        val b = LargeTopBarDefaults.dimens()
        assertEquals(a, b)
    }

    @Test
    fun dimenEquality_differentTitleTopSpacing_areNotEqual() {
        val a = LargeTopBarDefaults.dimens()
        val b = LargeTopBarDefaults.dimens(titleTopSpacing = 32.dp)
        assertNotEquals(a, b)
    }

    @Test
    fun custom_titleTopSpacing_overridesDefault() {
        val dimens = LargeTopBarDefaults.dimens(titleTopSpacing = 32.dp)
        assertEquals(32.dp, dimens.titleTopSpacing)
        assertEquals(40.dp, dimens.navigationButtonSize)
        assertEquals(PaddingValues(16.dp), dimens.contentPadding)
    }

    @Test
    fun custom_navigationButtonSize_overridesDefault() {
        val dimens = LargeTopBarDefaults.dimens(navigationButtonSize = 48.dp)
        assertEquals(48.dp, dimens.navigationButtonSize)
        assertEquals(20.dp, dimens.titleTopSpacing)
    }

    @Test
    fun custom_contentPadding_overridesDefault() {
        val customPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
        val dimens = LargeTopBarDefaults.dimens(contentPadding = customPadding)
        assertEquals(customPadding, dimens.contentPadding)
        assertEquals(40.dp, dimens.navigationButtonSize)
        assertEquals(20.dp, dimens.titleTopSpacing)
    }

    @Test
    fun copy_overridesOnlySpecifiedFields() {
        val original = LargeTopBarDefaults.dimens()
        val copied = original.copy(titleTopSpacing = 16.dp)
        assertEquals(16.dp, copied.titleTopSpacing)
        assertEquals(original.contentPadding, copied.contentPadding)
        assertEquals(original.navigationButtonSize, copied.navigationButtonSize)
    }

    @Test
    fun copy_withNoArgs_returnsEqualInstance() {
        val original = LargeTopBarDefaults.dimens()
        assertEquals(original, original.copy())
    }
}
