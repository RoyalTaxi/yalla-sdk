package uz.yalla.composites.view

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class EmptyStateColorsTest {

    private val colors = EmptyStateColors(
        title = Color.Black,
        description = Color.Gray,
    )

    @Test
    fun equality_sameValues_areEqual() {
        val other = EmptyStateColors(
            title = Color.Black,
            description = Color.Gray,
        )
        assertEquals(colors, other)
    }

    @Test
    fun equality_differentTitle_areNotEqual() {
        val other = colors.copy(title = Color.Red)
        assertNotEquals(colors, other)
    }

    @Test
    fun equality_differentDescription_areNotEqual() {
        val other = colors.copy(description = Color.Blue)
        assertNotEquals(colors, other)
    }

    @Test
    fun copy_overridesOnlySpecifiedFields() {
        val copied = colors.copy(description = Color.Blue)
        assertEquals(Color.Blue, copied.description)
        assertEquals(colors.title, copied.title)
    }

    @Test
    fun copy_withNoArgs_returnsEqualInstance() {
        assertEquals(colors, colors.copy())
    }
}

class EmptyStateDimensTest {

    @Test
    fun defaults_areNonNull() {
        val dimens = EmptyStateDefaults.dimens()
        assertNotNull(dimens)
    }

    @Test
    fun defaults_imageHeight_is180dp() {
        val dimens = EmptyStateDefaults.dimens()
        assertEquals(180.dp, dimens.imageHeight)
    }

    @Test
    fun defaults_imageTitleSpacing_is24dp() {
        val dimens = EmptyStateDefaults.dimens()
        assertEquals(24.dp, dimens.imageTitleSpacing)
    }

    @Test
    fun defaults_titleDescriptionSpacing_is8dp() {
        val dimens = EmptyStateDefaults.dimens()
        assertEquals(8.dp, dimens.titleDescriptionSpacing)
    }

    @Test
    fun defaults_descriptionActionSpacing_is24dp() {
        val dimens = EmptyStateDefaults.dimens()
        assertEquals(24.dp, dimens.descriptionActionSpacing)
    }

    @Test
    fun defaults_contentPadding_matchesExpected() {
        val dimens = EmptyStateDefaults.dimens()
        assertEquals(
            PaddingValues(horizontal = 32.dp, vertical = 48.dp),
            dimens.contentPadding,
        )
    }

    @Test
    fun dimenEquality_sameValues_areEqual() {
        val a = EmptyStateDefaults.dimens()
        val b = EmptyStateDefaults.dimens()
        assertEquals(a, b)
    }

    @Test
    fun dimenEquality_differentImageHeight_areNotEqual() {
        val a = EmptyStateDefaults.dimens()
        val b = EmptyStateDefaults.dimens(imageHeight = 200.dp)
        assertNotEquals(a, b)
    }

    @Test
    fun custom_imageHeight_overridesDefault() {
        val dimens = EmptyStateDefaults.dimens(imageHeight = 200.dp)
        assertEquals(200.dp, dimens.imageHeight)
        assertEquals(24.dp, dimens.imageTitleSpacing)
    }

    @Test
    fun custom_allSpacings_override() {
        val dimens = EmptyStateDefaults.dimens(
            imageTitleSpacing = 16.dp,
            titleDescriptionSpacing = 4.dp,
            descriptionActionSpacing = 16.dp,
        )
        assertEquals(16.dp, dimens.imageTitleSpacing)
        assertEquals(4.dp, dimens.titleDescriptionSpacing)
        assertEquals(16.dp, dimens.descriptionActionSpacing)
        assertEquals(180.dp, dimens.imageHeight)
    }

    @Test
    fun copy_overridesOnlySpecifiedFields() {
        val original = EmptyStateDefaults.dimens()
        val copied = original.copy(imageHeight = 240.dp)
        assertEquals(240.dp, copied.imageHeight)
        assertEquals(original.imageTitleSpacing, copied.imageTitleSpacing)
        assertEquals(original.titleDescriptionSpacing, copied.titleDescriptionSpacing)
        assertEquals(original.descriptionActionSpacing, copied.descriptionActionSpacing)
        assertEquals(original.contentPadding, copied.contentPadding)
    }
}
