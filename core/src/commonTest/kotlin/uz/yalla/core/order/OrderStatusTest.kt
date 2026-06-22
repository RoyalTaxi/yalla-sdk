package uz.yalla.core.order

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OrderStatusTest {
    @Test
    fun fromDecodesEveryKnownId() {
        assertEquals(OrderStatus.New, OrderStatus.from("new"))
        assertEquals(OrderStatus.Sending, OrderStatus.from("sending"))
        assertEquals(OrderStatus.UserSending, OrderStatus.from("user_sending"))
        assertEquals(OrderStatus.NonStopSending, OrderStatus.from("nonstop_sending"))
        assertEquals(OrderStatus.Appointed, OrderStatus.from("appointed"))
        assertEquals(OrderStatus.AtAddress, OrderStatus.from("at_address"))
        assertEquals(OrderStatus.InProgress, OrderStatus.from("in_progress"))
        assertEquals(OrderStatus.Completed, OrderStatus.from("completed"))
        assertEquals(OrderStatus.Canceled, OrderStatus.from("canceled"))
    }

    @Test
    fun fromAliasesInFettersToInProgress() {
        assertEquals(OrderStatus.InProgress, OrderStatus.from("in_fetters"))
        assertEquals(OrderStatus.from("in_progress"), OrderStatus.from("in_fetters"))
    }

    @Test
    fun fromTrimsAndLowercasesBeforeMatching() {
        assertEquals(OrderStatus.Appointed, OrderStatus.from("  appointed  "))
        assertEquals(OrderStatus.Appointed, OrderStatus.from("APPOINTED"))
        assertEquals(OrderStatus.Appointed, OrderStatus.from("  ApPoInTeD\n"))
        assertEquals(OrderStatus.InProgress, OrderStatus.from("  IN_FETTERS  "))
    }

    @Test
    fun fromNullDecodesToUnknownWithLiteralNullId() {
        assertEquals(OrderStatus.Unknown("null"), OrderStatus.from(null))
    }

    @Test
    fun fromUnrecognizedIdDecodesToUnknownPreservingOriginal() {
        assertEquals(OrderStatus.Unknown("paid"), OrderStatus.from("paid"))
        assertEquals(OrderStatus.Unknown("???"), OrderStatus.from("???"))
        assertEquals(OrderStatus.Unknown(""), OrderStatus.from(""))
    }

    @Test
    fun fromUnknownPreservesRawCasingAndWhitespaceNotTheNormalizedForm() {
        val raw = "  Mystery_State  "
        assertEquals(OrderStatus.Unknown(raw), OrderStatus.from(raw))
    }

    @Test
    fun fromIsStableAcrossRepeatedCalls() {
        assertEquals(OrderStatus.from("completed"), OrderStatus.from("completed"))
        assertEquals(OrderStatus.from(null), OrderStatus.from(null))
    }

    @Test
    fun idMatchesTheWireValueForEveryStatus() {
        assertEquals("new", OrderStatus.New.id)
        assertEquals("sending", OrderStatus.Sending.id)
        assertEquals("user_sending", OrderStatus.UserSending.id)
        assertEquals("nonstop_sending", OrderStatus.NonStopSending.id)
        assertEquals("appointed", OrderStatus.Appointed.id)
        assertEquals("at_address", OrderStatus.AtAddress.id)
        assertEquals("in_progress", OrderStatus.InProgress.id)
        assertEquals("completed", OrderStatus.Completed.id)
        assertEquals("canceled", OrderStatus.Canceled.id)
        assertEquals("unknown", OrderStatus.Unknown("anything").id)
    }

    @Test
    fun activeIsExactlyAppointedAtAddressInProgress() {
        assertEquals(
            setOf(OrderStatus.Appointed, OrderStatus.AtAddress, OrderStatus.InProgress),
            OrderStatus.active
        )
    }

    @Test
    fun ongoingCoversEveryPreCompletionStatus() {
        assertEquals(
            setOf(
                OrderStatus.New,
                OrderStatus.Sending,
                OrderStatus.UserSending,
                OrderStatus.NonStopSending,
                OrderStatus.Appointed,
                OrderStatus.AtAddress,
                OrderStatus.InProgress
            ),
            OrderStatus.ongoing
        )
    }

    @Test
    fun ongoingExcludesTerminalStatuses() {
        assertTrue(OrderStatus.Completed !in OrderStatus.ongoing)
        assertTrue(OrderStatus.Canceled !in OrderStatus.ongoing)
    }

    @Test
    fun nonInteractiveIsExactlyTheSearchingStatuses() {
        assertEquals(
            setOf(
                OrderStatus.New,
                OrderStatus.Sending,
                OrderStatus.UserSending,
                OrderStatus.NonStopSending
            ),
            OrderStatus.nonInteractive
        )
    }

    @Test
    fun partitionsRelateAsExpected() {
        assertEquals(OrderStatus.ongoing - OrderStatus.nonInteractive, OrderStatus.active)
        assertTrue(OrderStatus.active.all { it in OrderStatus.ongoing })
        assertTrue(OrderStatus.nonInteractive.all { it in OrderStatus.ongoing })
    }
}
