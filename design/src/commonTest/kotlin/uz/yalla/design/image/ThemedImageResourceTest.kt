package uz.yalla.design.image

import kotlin.test.Test
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Pins the pure light/dark selection that backs [rememberThemedPainter]. The selection is the whole point
 * of [ThemedImage], yet it sits behind a `@Composable` that cannot be unit-tested; extracting
 * [resourceFor] (mirroring how `CardBrandPresentationTest` extracts the non-`@Composable` half)
 * lets both branches be guarded against an inverted condition that would ship light art in dark mode.
 */
class ThemedImageResourceTest {
    @Test
    fun darkBranchReturnsDarkResource() {
        for (image in ThemedImage.entries) {
            assertSame(image.dark, image.resourceFor(isDark = true), "dark branch for $image")
        }
    }

    @Test
    fun lightBranchReturnsLightResource() {
        for (image in ThemedImage.entries) {
            assertSame(image.light, image.resourceFor(isDark = false), "light branch for $image")
        }
    }

    @Test
    fun assetNameIsPresentForEveryEntry() {
        // The iOS bridge resolves images by this string; a blank one silently fails the lookup.
        for (image in ThemedImage.entries) {
            assertTrue(image.assetName.isNotBlank(), "assetName for $image")
        }
    }
}
