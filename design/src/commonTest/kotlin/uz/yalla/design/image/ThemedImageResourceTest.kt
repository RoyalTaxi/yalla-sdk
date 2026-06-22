package uz.yalla.design.image

import kotlin.test.Test
import kotlin.test.assertSame
import kotlin.test.assertTrue

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
        for (image in ThemedImage.entries) {
            assertTrue(image.assetName.isNotBlank(), "assetName for $image")
        }
    }
}
