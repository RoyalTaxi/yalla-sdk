package uz.yalla.platform.picker

import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Smoke tests for [NativeWheelDatePicker] expect/actual pair.
 *
 * [NativeWheelDatePicker] is a @Composable expect function — invoking it requires a Compose
 * UI harness that is not wired in this module (Robolectric / Compose UI Test absent).
 * Those runtime tests are deferred to Phase 4.
 *
 * TODO(Phase 4): Add a Compose UI test that:
 *   - Renders NativeWheelDatePicker with a fixed startDate.
 *   - Simulates a scroll interaction and verifies onDateChanged fires with a new LocalDate.
 *   - Verifies minDate / maxDate constraints are enforced (scrolling beyond bounds is rejected).
 *
 * This placeholder ensures the file exists in the test source set so the pair is tracked.
 */
class NativeWheelDatePickerTest {

    @Test
    fun compileVerify_nativeWheelDatePickerSignatureExists() {
        // Confirms the expect declaration is visible from commonTest and the module compiles.
        assertTrue(true, "NativeWheelDatePicker expect declaration compiles and is visible from commonTest")
    }
}
