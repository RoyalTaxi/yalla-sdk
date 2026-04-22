package uz.yalla.composites.drawer

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class NavigableColorsTest {

    private val colors = NavigableColors(
        container = Color.White,
        title = Color.Black,
        description = Color.Gray,
        chevron = Color.DarkGray,
    )

    @Test
    fun equality_sameValues_areEqual() {
        val other = NavigableColors(
            container = Color.White,
            title = Color.Black,
            description = Color.Gray,
            chevron = Color.DarkGray,
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
    fun equality_differentDescription_areNotEqual() {
        val other = colors.copy(description = Color.Cyan)
        assertNotEquals(colors, other)
    }

    @Test
    fun equality_differentChevron_areNotEqual() {
        val other = colors.copy(chevron = Color.Yellow)
        assertNotEquals(colors, other)
    }

    @Test
    fun copy_overridesOnlySpecifiedFields() {
        val copied = colors.copy(title = Color.Blue)
        assertEquals(Color.Blue, copied.title)
        assertEquals(colors.container, copied.container)
        assertEquals(colors.description, copied.description)
        assertEquals(colors.chevron, copied.chevron)
    }

    @Test
    fun copy_withNoArgs_returnsEqualInstance() {
        assertEquals(colors, colors.copy())
    }
}

class NavigableDimensTest {

    @Test
    fun defaults_areNonNull() {
        val dimens = NavigableDefaults.dimens()
        assertNotNull(dimens)
    }

    @Test
    fun defaults_iconSpacing_is6dp() {
        val dimens = NavigableDefaults.dimens()
        assertEquals(6.dp, dimens.iconSpacing)
    }

    @Test
    fun defaults_descriptionSpacing_is4dp() {
        val dimens = NavigableDefaults.dimens()
        assertEquals(4.dp, dimens.descriptionSpacing)
    }

    @Test
    fun defaults_trailingSpacing_is8dp() {
        val dimens = NavigableDefaults.dimens()
        assertEquals(8.dp, dimens.trailingSpacing)
    }

    @Test
    fun defaults_chevronSize_is24dp() {
        val dimens = NavigableDefaults.dimens()
        assertEquals(24.dp, dimens.chevronSize)
    }

    @Test
    fun defaults_contentPadding_matchesExpected() {
        val dimens = NavigableDefaults.dimens()
        assertEquals(
            PaddingValues(horizontal = 8.dp, vertical = 9.dp),
            dimens.contentPadding,
        )
    }

    @Test
    fun dimenEquality_sameValues_areEqual() {
        val a = NavigableDefaults.dimens()
        val b = NavigableDefaults.dimens()
        assertEquals(a, b)
    }

    @Test
    fun dimenEquality_differentChevronSize_areNotEqual() {
        val a = NavigableDefaults.dimens()
        val b = NavigableDefaults.dimens(chevronSize = 16.dp)
        assertNotEquals(a, b)
    }

    @Test
    fun custom_chevronSize_overridesDefault() {
        val dimens = NavigableDefaults.dimens(chevronSize = 16.dp)
        assertEquals(16.dp, dimens.chevronSize)
        assertEquals(6.dp, dimens.iconSpacing)
    }

    @Test
    fun copy_overridesOnlySpecifiedFields() {
        val original = NavigableDefaults.dimens()
        val copied = original.copy(trailingSpacing = 12.dp)
        assertEquals(12.dp, copied.trailingSpacing)
        assertEquals(original.iconSpacing, copied.iconSpacing)
        assertEquals(original.descriptionSpacing, copied.descriptionSpacing)
        assertEquals(original.chevronSize, copied.chevronSize)
        assertEquals(original.contentPadding, copied.contentPadding)
    }
}
