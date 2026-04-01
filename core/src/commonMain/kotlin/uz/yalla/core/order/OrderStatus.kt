package uz.yalla.core.order

/**
 * Order lifecycle state machine.
 *
 * Represents all possible states of a taxi order from creation through completion.
 * Use companion object sets ([active], [ongoing], [nonInteractive]) to check order
 * state groups without exhaustive when-expressions.
 *
 * ## Lifecycle
 * ```
 * New → Sending/UserSending/NonStopSending → Appointed → AtAddress → InProgress → Completed
 *                                                                                 → Canceled
 * ```
 *
 * @property id Wire-format identifier used in API communication
 * @since 0.0.1
 */
sealed class OrderStatus(val id: String) {
    data object New : OrderStatus("new")

    data object Sending : OrderStatus("sending")

    data object UserSending : OrderStatus("user_sending")

    data object NonStopSending : OrderStatus("nonstop_sending")

    data object Appointed : OrderStatus("appointed")

    data object AtAddress : OrderStatus("at_address")

    data object InProgress : OrderStatus("in_progress")

    data object Completed : OrderStatus("completed")

    data object Canceled : OrderStatus("canceled")

    /** Unrecognized status from API, preserves [originalId] for debugging. */
    data class Unknown(val originalId: String) : OrderStatus("unknown")

    companion object {
        /**
         * Parses an API status string into the corresponding [OrderStatus].
         *
         * Performs case-insensitive matching after trimming whitespace.
         * The `"in_fetters"` alias is mapped to [InProgress] for legacy API compatibility.
         * Returns [Unknown] for `null` or unrecognized values.
         *
         * @param id Wire-format status identifier from the API, or `null`
         * @return The matching [OrderStatus], or [Unknown] with the original value preserved
         */
        fun from(id: String?): OrderStatus =
            when (id?.trim()?.lowercase()) {
                "new" -> New
                "sending" -> Sending
                "user_sending" -> UserSending
                "nonstop_sending" -> NonStopSending
                "appointed" -> Appointed
                "at_address" -> AtAddress
                "in_progress", "in_fetters" -> InProgress
                "completed" -> Completed
                "canceled" -> Canceled
                else -> Unknown(id ?: "null")
            }

        /** Statuses where a driver has been assigned (order is active with driver) */
        val active: Set<OrderStatus> =
            setOf(
                Appointed,
                AtAddress,
                InProgress
            )

        /** All non-finished statuses (order exists and not completed/canceled) */
        val ongoing: Set<OrderStatus> =
            setOf(
                New,
                Sending,
                UserSending,
                NonStopSending,
                Appointed,
                AtAddress,
                InProgress
            )

        /** Statuses where order is being processed/sent but no driver assigned yet */
        val nonInteractive: Set<OrderStatus> =
            setOf(
                New,
                Sending,
                UserSending,
                NonStopSending
            )
    }
}
