package uz.yalla.core.order

import kotlin.test.Test
import kotlin.test.assertEquals

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
        assertEquals(ExtraService.CostType.Fixed, ExtraService.CostType.from("fixed"))
        assertEquals(ExtraService.CostType.Fixed, ExtraService.CostType.from("ratio"))
    }

    @Test
    fun idsAreStable() {
        assertEquals("cost", ExtraService.CostType.Fixed.id)
        assertEquals("percent", ExtraService.CostType.Percent.id)
    }
}
