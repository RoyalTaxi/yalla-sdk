package uz.yalla.core.order

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Characterization of [OrderStatus.from] and the status partitions.
 *
 * Highest cost-of-silent-break in the pure core: `from` carries a non-obvious aliasing
 * contract (both `in_progress` AND `in_fetters` decode to [OrderStatus.InProgress]) and an
 * [OrderStatus.Unknown] fallback that preserves the original id. A careless edit could erase
 * either silently, so every branch — and the input normalization around it — is pinned here.
 */
class OrderStatusTest {

    @Test
    fun from_decodesEveryKnownId() {
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
    fun from_aliasesInFettersToInProgress() {
        // The contract: the legacy backend id `in_fetters` must collapse onto InProgress.
        assertEquals(OrderStatus.InProgress, OrderStatus.from("in_fetters"))
        assertEquals(OrderStatus.from("in_progress"), OrderStatus.from("in_fetters"))
    }

    @Test
    fun from_trimsAndLowercasesBeforeMatching() {
        assertEquals(OrderStatus.Appointed, OrderStatus.from("  appointed  "))
        assertEquals(OrderStatus.Appointed, OrderStatus.from("APPOINTED"))
        assertEquals(OrderStatus.Appointed, OrderStatus.from("  ApPoInTeD\n"))
        assertEquals(OrderStatus.InProgress, OrderStatus.from("  IN_FETTERS  "))
    }

    @Test
    fun from_nullDecodesToUnknownWithLiteralNullId() {
        assertEquals(OrderStatus.Unknown("null"), OrderStatus.from(null))
    }

    @Test
    fun from_unrecognizedIdDecodesToUnknownPreservingOriginal() {
        assertEquals(OrderStatus.Unknown("paid"), OrderStatus.from("paid"))
        assertEquals(OrderStatus.Unknown("???"), OrderStatus.from("???"))
        assertEquals(OrderStatus.Unknown(""), OrderStatus.from(""))
    }

    @Test
    fun from_unknownPreservesRawCasingAndWhitespaceNotTheNormalizedForm() {
        // Normalization is only applied to the match key; the fallback keeps the raw input.
        val raw = "  Mystery_State  "
        assertEquals(OrderStatus.Unknown(raw), OrderStatus.from(raw))
    }

    @Test
    fun from_isStableAcrossRepeatedCalls() {
        assertEquals(OrderStatus.from("completed"), OrderStatus.from("completed"))
        assertEquals(OrderStatus.from(null), OrderStatus.from(null))
    }

    @Test
    fun id_matchesTheWireValueForEveryStatus() {
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
    fun active_isExactlyAppointedAtAddressInProgress() {
        assertEquals(
            setOf(OrderStatus.Appointed, OrderStatus.AtAddress, OrderStatus.InProgress),
            OrderStatus.active
        )
    }

    @Test
    fun ongoing_coversEveryPreCompletionStatus() {
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
    fun ongoing_excludesTerminalStatuses() {
        assertTrue(OrderStatus.Completed !in OrderStatus.ongoing)
        assertTrue(OrderStatus.Canceled !in OrderStatus.ongoing)
    }

    @Test
    fun nonInteractive_isExactlyTheSearchingStatuses() {
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
    fun partitions_relateAsExpected() {
        // active is the ongoing statuses that are NOT in the non-interactive (searching) phase.
        assertEquals(OrderStatus.ongoing - OrderStatus.nonInteractive, OrderStatus.active)
        assertTrue(OrderStatus.active.all { it in OrderStatus.ongoing })
        assertTrue(OrderStatus.nonInteractive.all { it in OrderStatus.ongoing })
    }
}
