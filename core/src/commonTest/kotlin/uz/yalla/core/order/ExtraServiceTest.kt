package uz.yalla.core.order

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ExtraServiceTest {
    @Test
    fun shouldReturnTrueWhenCostTypeIsPercentIgnoringCase() {
        val service =
            ExtraService(
                id = 1,
                cost = 10,
                name = "Service",
                costType = "PeRcEnT"
            )

        assertTrue(service.isPercentCost)
    }

    @Test
    fun shouldReturnFalseWhenCostTypeIsNotPercent() {
        val service =
            ExtraService(
                id = 1,
                cost = 10,
                name = "Service",
                costType = ExtraService.COST_TYPE_COST
            )

        assertFalse(service.isPercentCost)
    }
}
