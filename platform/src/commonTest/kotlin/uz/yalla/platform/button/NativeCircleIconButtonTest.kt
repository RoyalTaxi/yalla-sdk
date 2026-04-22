package uz.yalla.platform.button

import uz.yalla.platform.model.IconType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Smoke tests for [NativeCircleIconButton] expect/actual pair.
 *
 * [NativeCircleIconButton] is a @Composable expect function — calling it requires a
 * Compose UI harness (Robolectric / Compose UI Test) that is not wired in this module.
 * Those runtime tests are deferred to Phase 4.
 *
 * TODO(Phase 4): Add a Compose UI test that:
 *   - Renders NativeCircleIconButton inside a setContent block.
 *   - Performs a click interaction and verifies the onClick lambda is invoked exactly once.
 *   - Checks that the Modifier and border params are forwarded without crashing.
 *
 * This file verifies [IconType], which is the key parameter type, at compile-and-run level.
 */
class NativeCircleIconButtonTest {

    @Test
    fun shouldExposeEightIconTypeVariants() {
        // IconType is the core parameter of NativeCircleIconButton; its count must be stable.
        assertEquals(8, IconType.entries.size, "IconType must have exactly 8 entries")
    }

    @Test
    fun shouldExposeAllDocumentedIconTypes() {
        assertNotNull(IconType.MENU)
        assertNotNull(IconType.CLOSE)
        assertNotNull(IconType.DONE)
        assertNotNull(IconType.BACK)
        assertNotNull(IconType.FOCUS_LOCATION)
        assertNotNull(IconType.FOCUS_ROUTE)
        assertNotNull(IconType.FOCUS_ORIGIN)
        assertNotNull(IconType.FOCUS_DESTINATION)
    }
}
