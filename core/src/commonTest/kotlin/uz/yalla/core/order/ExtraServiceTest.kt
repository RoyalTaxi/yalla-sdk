package uz.yalla.core.order

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class ExtraServiceTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun shouldSerializeFixedCostTypeAsCost() {
        val service = ExtraService(id = 1, cost = 5000, name = "Child seat", costType = ExtraService.CostType.Fixed)

        val encoded = json.encodeToString(service)

        assertEquals(true, encoded.contains("\"costType\":\"cost\""))
    }

    @Test
    fun shouldSerializePercentCostTypeAsPercent() {
        val service = ExtraService(id = 2, cost = 10, name = "Pet fee", costType = ExtraService.CostType.Percent)

        val encoded = json.encodeToString(service)

        assertEquals(true, encoded.contains("\"costType\":\"percent\""))
    }

    @Test
    fun shouldDeserializeCostStringIntoFixed() {
        val raw = """{"id":1,"cost":5000,"name":"Child seat","costType":"cost"}"""

        val decoded = json.decodeFromString<ExtraService>(raw)

        assertEquals(ExtraService.CostType.Fixed, decoded.costType)
    }

    @Test
    fun shouldDeserializePercentStringIntoPercent() {
        val raw = """{"id":2,"cost":10,"name":"Pet fee","costType":"percent"}"""

        val decoded = json.decodeFromString<ExtraService>(raw)

        assertEquals(ExtraService.CostType.Percent, decoded.costType)
    }
}
