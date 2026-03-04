package uz.yalla.core.order

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class OrderStatusTest {
    @Test
    fun shouldMapAllKnownStatusesFromId() {
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
    fun shouldMapInProgressWhenIdIsInFettersAlias() {
        val status = OrderStatus.from("in_fetters")

        assertEquals(OrderStatus.InProgress, status)
    }

    @Test
    fun shouldTrimAndLowercaseIdBeforeMatchingKnownStatus() {
        val status = OrderStatus.from("  COMPLETED  ")

        assertEquals(OrderStatus.Completed, status)
    }

    @Test
    fun shouldReturnUnknownWithOriginalIdWhenStatusIsNotRecognized() {
        val rawId = " custom_status "

        val status = OrderStatus.from(rawId)

        val unknown = assertIs<OrderStatus.Unknown>(status)
        assertEquals(rawId, unknown.originalId)
    }

    @Test
    fun shouldReturnUnknownWithNullLiteralWhenStatusIdIsNull() {
        val status = OrderStatus.from(null)

        val unknown = assertIs<OrderStatus.Unknown>(status)
        assertEquals("null", unknown.originalId)
    }

    @Test
    fun shouldContainOnlyExpectedActiveStatuses() {
        val expected =
            setOf(
                OrderStatus.Appointed,
                OrderStatus.AtAddress,
                OrderStatus.InProgress
            )

        assertEquals(expected, OrderStatus.active)
    }

    @Test
    fun shouldContainOnlyExpectedOngoingStatuses() {
        val expected =
            setOf(
                OrderStatus.New,
                OrderStatus.Sending,
                OrderStatus.UserSending,
                OrderStatus.NonStopSending,
                OrderStatus.Appointed,
                OrderStatus.AtAddress,
                OrderStatus.InProgress
            )

        assertEquals(expected, OrderStatus.ongoing)
    }

    @Test
    fun shouldContainOnlyExpectedNonInteractiveStatuses() {
        val expected =
            setOf(
                OrderStatus.New,
                OrderStatus.Sending,
                OrderStatus.UserSending,
                OrderStatus.NonStopSending
            )

        assertEquals(expected, OrderStatus.nonInteractive)
    }
}
