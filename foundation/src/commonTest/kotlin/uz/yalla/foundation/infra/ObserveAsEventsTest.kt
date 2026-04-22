package uz.yalla.foundation.infra

import kotlin.test.Test

/**
 * Tests for [ObserveAsEvents].
 *
 * TODO(Task 5): ObserveAsEvents is a @Composable that uses LocalLifecycleOwner +
 *   LaunchedEffect + repeatOnLifecycle(minState). Testing it correctly requires the
 *   Compose UI test harness (compose-ui-test / createComposeRule / TestLifecycleOwner).
 *
 *   The foundation module currently has no compose-ui-test dependency wired:
 *     - build.gradle.kts commonTest block only has kotlinx-coroutines-test and turbine.
 *     - Adding compose-ui-test would pull in android-instrumentation infra not supported
 *       by the AGP KMP plugin's allTests runner (iosSimulatorArm64Test).
 *
 *   The production logic is straightforward — it delegates entirely to
 *   repeatOnLifecycle(minState) from androidx-lifecycle-runtime-ktx, which is itself
 *   well-tested upstream. A meaningful test would verify:
 *     1. Events emitted while lifecycle >= minState are delivered to onEvent.
 *     2. Events emitted while lifecycle < minState are dropped (not buffered).
 *
 *   These can be added when compose-ui-test is wired into the module's androidTest variant.
 */
class ObserveAsEventsTest {

    @Test
    fun placeholder_compileCheck() {
        // Intentionally empty — ensures this file compiles on both iOS and future Android targets.
        // Real assertions are blocked by missing Compose UI test harness (see class KDoc).
    }
}
