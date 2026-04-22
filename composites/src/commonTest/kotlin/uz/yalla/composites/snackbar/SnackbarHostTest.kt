package uz.yalla.composites.snackbar

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class SnackbarDataTest {

    @Test
    fun defaultIsSuccess_isTrue() {
        val data = SnackbarData(message = "Hello")
        assertEquals(true, data.isSuccess)
    }

    @Test
    fun explicitIsSuccess_false_isStored() {
        val data = SnackbarData(message = "Error!", isSuccess = false)
        assertEquals(false, data.isSuccess)
    }

    @Test
    fun equality_sameValues_areEqual() {
        val a = SnackbarData(message = "Hello", isSuccess = true)
        val b = SnackbarData(message = "Hello", isSuccess = true)
        assertEquals(a, b)
    }

    @Test
    fun equality_differentMessage_areNotEqual() {
        val a = SnackbarData(message = "Hello", isSuccess = true)
        val b = SnackbarData(message = "World", isSuccess = true)
        assertNotEquals(a, b)
    }

    @Test
    fun equality_differentIsSuccess_areNotEqual() {
        val a = SnackbarData(message = "Hello", isSuccess = true)
        val b = SnackbarData(message = "Hello", isSuccess = false)
        assertNotEquals(a, b)
    }

    @Test
    fun copy_overridesOnlySpecifiedFields() {
        val original = SnackbarData(message = "Original", isSuccess = true)
        val copied = original.copy(isSuccess = false)
        assertEquals(false, copied.isSuccess)
        assertEquals("Original", copied.message)
    }

    @Test
    fun copy_withNoArgs_returnsEqualInstance() {
        val original = SnackbarData(message = "Hello", isSuccess = true)
        assertEquals(original, original.copy())
    }
}

class SnackbarHostDimensTest {

    @Test
    fun defaults_areNonNull() {
        val dimens = SnackbarHostDefaults.dimens()
        assertNotNull(dimens)
    }

    @Test
    fun defaults_topPadding_is8dp() {
        val dimens = SnackbarHostDefaults.dimens()
        assertEquals(8.dp, dimens.topPadding)
    }

    @Test
    fun defaults_horizontalPadding_is16dp() {
        val dimens = SnackbarHostDefaults.dimens()
        assertEquals(16.dp, dimens.horizontalPadding)
    }

    @Test
    fun dimenEquality_sameValues_areEqual() {
        val a = SnackbarHostDefaults.dimens()
        val b = SnackbarHostDefaults.dimens()
        assertEquals(a, b)
    }

    @Test
    fun dimenEquality_differentTopPadding_areNotEqual() {
        val a = SnackbarHostDefaults.dimens()
        val b = SnackbarHostDefaults.dimens(topPadding = 16.dp)
        assertNotEquals(a, b)
    }

    @Test
    fun custom_topPadding_overridesDefault() {
        val dimens = SnackbarHostDefaults.dimens(topPadding = 24.dp)
        assertEquals(24.dp, dimens.topPadding)
        assertEquals(16.dp, dimens.horizontalPadding)
    }

    @Test
    fun custom_horizontalPadding_overridesDefault() {
        val dimens = SnackbarHostDefaults.dimens(horizontalPadding = 24.dp)
        assertEquals(24.dp, dimens.horizontalPadding)
        assertEquals(8.dp, dimens.topPadding)
    }

    @Test
    fun dataClass_copy_overridesOnlySpecifiedFields() {
        val original = SnackbarHostDimens(topPadding = 8.dp, horizontalPadding = 16.dp)
        val copied = original.copy(topPadding = 20.dp)
        assertEquals(20.dp, copied.topPadding)
        assertEquals(16.dp, copied.horizontalPadding)
    }
}
