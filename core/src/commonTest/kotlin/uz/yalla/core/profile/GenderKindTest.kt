package uz.yalla.core.profile

import kotlin.test.Test
import kotlin.test.assertEquals

class GenderKindTest {
    @Test
    fun shouldReturnGenderWhenIdMatches() {
        val gender = GenderKind.from("male")

        assertEquals(GenderKind.Male, gender)
    }

    @Test
    fun shouldNormalizeIdWhenFindingGender() {
        val gender = GenderKind.from("  FEMALE  ")

        assertEquals(GenderKind.Female, gender)
    }

    @Test
    fun shouldReturnNotSelectedWhenIdIsUnknownOrNull() {
        assertEquals(GenderKind.NotSelected, GenderKind.from("unknown"))
        assertEquals(GenderKind.NotSelected, GenderKind.from(null))
    }
}
