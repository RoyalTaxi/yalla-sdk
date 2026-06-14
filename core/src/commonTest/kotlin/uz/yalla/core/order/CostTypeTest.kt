package uz.yalla.core.order

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Characterization of [ExtraService.CostType.from].
 *
 * The non-obvious bit pinned here: the wire id for [ExtraService.CostType.Fixed] is "cost"
 * (NOT "fixed"), and the fallback for anything unrecognized is also Fixed. So a tariff line whose
 * type fails to decode is treated as a fixed surcharge, never a percentage — a careless rename of
 * the "cost" id would silently turn fixed fees into the fallback while still landing on Fixed.
 */
class CostTypeTest {
    @Test
    fun decodesKnownWireIds() {
        assertEquals(ExtraService.CostType.Fixed, ExtraService.CostType.from("cost"))
        assertEquals(ExtraService.CostType.Percent, ExtraService.CostType.from("percent"))
    }

    @Test
    fun normalizesCaseAndWhitespace() {
        assertEquals(ExtraService.CostType.Fixed, ExtraService.CostType.from("  COST  "))
        assertEquals(ExtraService.CostType.Percent, ExtraService.CostType.from("Percent"))
    }

    @Test
    fun fallsBackToFixedForUnknownAndNull() {
        assertEquals(ExtraService.CostType.Fixed, ExtraService.CostType.from(null))
        assertEquals(ExtraService.CostType.Fixed, ExtraService.CostType.from(""))
        // "fixed" is NOT the wire id; it falls through to the Fixed default coincidentally.
        assertEquals(ExtraService.CostType.Fixed, ExtraService.CostType.from("fixed"))
        assertEquals(ExtraService.CostType.Fixed, ExtraService.CostType.from("ratio"))
    }

    @Test
    fun idsAreStable() {
        assertEquals("cost", ExtraService.CostType.Fixed.id)
        assertEquals("percent", ExtraService.CostType.Percent.id)
    }
}
