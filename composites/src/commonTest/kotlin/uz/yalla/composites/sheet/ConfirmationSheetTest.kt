package uz.yalla.composites.sheet

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ConfirmationSheetColorsTest {

    private val colors = ConfirmationSheetColors(
        container = Color.White,
        title = Color.Black,
        description = Color.Gray,
    )

    @Test
    fun equality_sameValues_areEqual() {
        val other = ConfirmationSheetColors(
            container = Color.White,
            title = Color.Black,
            description = Color.Gray,
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
        val copied = colors.copy(title = Color.Blue)
        assertEquals(Color.Blue, copied.title)
        assertEquals(colors.container, copied.container)
        assertEquals(colors.description, copied.description)
    }
}

class ConfirmationSheetDimensTest {

    @Test
    fun defaults_haveExpectedValues() {
        val dimens = ConfirmationSheetDefaults.dimens()
        assertEquals(RoundedCornerShape(topStart = 38.dp, topEnd = 38.dp), dimens.shape)
        assertEquals(10.dp, dimens.headerTopPadding)
        assertEquals(10.dp, dimens.headerHorizontalPadding)
        assertEquals(44.dp, dimens.contentTopPadding)
        assertEquals(36.dp, dimens.contentHorizontalPadding)
        assertEquals(0.6f, dimens.imageWidthFraction)
        assertEquals(36.dp, dimens.imageBottomSpacing)
        assertEquals(12.dp, dimens.titleDescriptionSpacing)
        assertEquals(64.dp, dimens.actionTopSpacing)
        assertEquals(20.dp, dimens.actionHorizontalPadding)
        assertEquals(12.dp, dimens.actionBottomSpacing)
    }

    @Test
    fun dimens_overridesWork() {
        val dimens = ConfirmationSheetDefaults.dimens(
            shape = RoundedCornerShape(16.dp),
            headerTopPadding = 16.dp,
            headerHorizontalPadding = 16.dp,
            contentTopPadding = 32.dp,
            contentHorizontalPadding = 24.dp,
            imageWidthFraction = 0.8f,
            imageBottomSpacing = 24.dp,
            titleDescriptionSpacing = 16.dp,
            actionTopSpacing = 48.dp,
            actionHorizontalPadding = 16.dp,
            actionBottomSpacing = 16.dp,
        )
        assertEquals(RoundedCornerShape(16.dp), dimens.shape)
        assertEquals(16.dp, dimens.headerTopPadding)
        assertEquals(16.dp, dimens.headerHorizontalPadding)
        assertEquals(32.dp, dimens.contentTopPadding)
        assertEquals(24.dp, dimens.contentHorizontalPadding)
        assertEquals(0.8f, dimens.imageWidthFraction)
        assertEquals(24.dp, dimens.imageBottomSpacing)
        assertEquals(16.dp, dimens.titleDescriptionSpacing)
        assertEquals(48.dp, dimens.actionTopSpacing)
        assertEquals(16.dp, dimens.actionHorizontalPadding)
        assertEquals(16.dp, dimens.actionBottomSpacing)
    }
}
