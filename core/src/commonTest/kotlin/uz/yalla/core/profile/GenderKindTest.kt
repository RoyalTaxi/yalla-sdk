package uz.yalla.core.profile

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Characterization of [GenderKind.from]: normalized (trim + lowercase) decode with a
 * [GenderKind.NotSelected] fallback for unknown and null input.
 */
class GenderKindTest {
    @Test
    fun decodesKnownIds() {
        assertEquals(GenderKind.Male, GenderKind.from("male"))
        assertEquals(GenderKind.Female, GenderKind.from("female"))
        assertEquals(GenderKind.NotSelected, GenderKind.from("not_selected"))
    }

    @Test
    fun normalizesCaseAndWhitespace() {
        assertEquals(GenderKind.Male, GenderKind.from("  MALE  "))
        assertEquals(GenderKind.Female, GenderKind.from("Female"))
    }

    @Test
    fun fallsBackToNotSelectedForUnknownAndNull() {
        assertEquals(GenderKind.NotSelected, GenderKind.from(null))
        assertEquals(GenderKind.NotSelected, GenderKind.from(""))
        assertEquals(GenderKind.NotSelected, GenderKind.from("other"))
    }

    @Test
    fun idsAreStable() {
        assertEquals("male", GenderKind.Male.id)
        assertEquals("female", GenderKind.Female.id)
        assertEquals("not_selected", GenderKind.NotSelected.id)
    }
}
