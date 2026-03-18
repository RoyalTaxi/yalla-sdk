package uz.yalla.design.image

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ThemedImageTest {

    @Test
    fun shouldHaveDistinctLightAndDarkResourcesForAllEntries() {
        ThemedImage.entries.forEach { image ->
            assertNotEquals(
                image.light,
                image.dark,
                "Expected distinct resources for ${image.name}: light and dark should differ",
            )
        }
    }

    @Test
    fun shouldHaveCorrectNumberOfEntries() {
        assertEquals(12, ThemedImage.entries.size)
    }
}
