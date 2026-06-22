package uz.yalla.telemetry.event

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AnalyticsEventTest {
    @Test
    fun eachPutOverloadProducesItsParamValueType() {
        val e =
            event("e") {
                put("text", "hello")
                put("long", 7L)
                put("int", 3)
                put("amount", 1.5)
                put("flag", true)
            }

        assertEquals(ParamValue.Text("hello"), e.params["text"])
        assertEquals(ParamValue.Count(7L), e.params["long"])
        assertEquals(ParamValue.Count(3L), e.params["int"])
        assertEquals(ParamValue.Amount(1.5), e.params["amount"])
        assertEquals(ParamValue.Flag(true), e.params["flag"])
    }

    @Test
    fun duplicateKeyKeepsLastValue() {
        val e =
            event("e") {
                put("k", "first")
                put("k", "second")
            }

        assertEquals(1, e.params.size)
        assertEquals(ParamValue.Text("second"), e.params["k"])
    }

    @Test
    fun paramOrderIsInsertionOrder() {
        val e =
            event("e") {
                put("c", 1)
                put("a", 2)
                put("b", 3)
            }

        assertEquals(listOf("c", "a", "b"), e.params.keys.toList())
    }

    @Test
    fun emptyBuilderProducesEmptyParams() {
        val e = event("e")

        assertEquals("e", e.name)
        assertTrue(e.params.isEmpty())
    }
}
