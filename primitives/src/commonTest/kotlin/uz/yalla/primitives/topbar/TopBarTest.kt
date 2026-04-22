package uz.yalla.primitives.topbar

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class TopBarColorsTest {

    private val colors = TopBarColors(
        container = Color.White,
        title = Color.Black,
    )

    @Test
    fun equality_sameValues_areEqual() {
        val other = TopBarColors(
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

class TopBarDimensTest {

    @Test
    fun defaults_areNonNull() {
        val dimens = TopBarDefaults.dimens()
        assertNotNull(dimens)
    }

    @Test
    fun defaults_navigationButtonSize_is40dp() {
        val dimens = TopBarDefaults.dimens()
        assertEquals(40.dp, dimens.navigationButtonSize)
    }

    @Test
    fun defaults_contentPadding_is16dp() {
        val dimens = TopBarDefaults.dimens()
        assertEquals(PaddingValues(16.dp), dimens.contentPadding)
    }

    @Test
    fun dimenEquality_sameValues_areEqual() {
        val a = TopBarDefaults.dimens()
        val b = TopBarDefaults.dimens()
        assertEquals(a, b)
    }

    @Test
    fun dimenEquality_differentNavigationButtonSize_areNotEqual() {
        val a = TopBarDefaults.dimens()
        val b = TopBarDefaults.dimens(navigationButtonSize = 48.dp)
        assertNotEquals(a, b)
    }

    @Test
    fun custom_navigationButtonSize_overridesDefault() {
        val dimens = TopBarDefaults.dimens(navigationButtonSize = 48.dp)
        assertEquals(48.dp, dimens.navigationButtonSize)
        assertEquals(PaddingValues(16.dp), dimens.contentPadding)
    }

    @Test
    fun custom_contentPadding_overridesDefault() {
        val customPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
        val dimens = TopBarDefaults.dimens(contentPadding = customPadding)
        assertEquals(customPadding, dimens.contentPadding)
        assertEquals(40.dp, dimens.navigationButtonSize)
    }

    @Test
    fun copy_overridesOnlySpecifiedFields() {
        val original = TopBarDefaults.dimens()
        val copied = original.copy(navigationButtonSize = 36.dp)
        assertEquals(36.dp, copied.navigationButtonSize)
        assertEquals(original.contentPadding, copied.contentPadding)
    }

    @Test
    fun copy_withNoArgs_returnsEqualInstance() {
        val original = TopBarDefaults.dimens()
        assertEquals(original, original.copy())
    }
}
