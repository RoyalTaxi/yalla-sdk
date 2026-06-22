package uz.yalla.core.order

import uz.yalla.core.util.normalizedId

public sealed class OrderStatus(
    public val id: String
) {
    public data object New : OrderStatus("new")

    public data object Sending : OrderStatus("sending")

    public data object UserSending : OrderStatus("user_sending")

    public data object NonStopSending : OrderStatus("nonstop_sending")

    public data object Appointed : OrderStatus("appointed")

    public data object AtAddress : OrderStatus("at_address")

    public data object InProgress : OrderStatus("in_progress")

    public data object Completed : OrderStatus("completed")

    public data object Canceled : OrderStatus("canceled")

    public data class Unknown(
        val originalId: String
    ) : OrderStatus("unknown")

    public companion object {
        public fun from(id: String?): OrderStatus =
            when (id.normalizedId()) {
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

        public val active: Set<OrderStatus> by lazy {
            setOf(
                Appointed,
                AtAddress,
                InProgress
            )
        }

        public val ongoing: Set<OrderStatus> by lazy {
            setOf(
                New,
                Sending,
                UserSending,
                NonStopSending,
                Appointed,
                AtAddress,
                InProgress
            )
        }

        public val nonInteractive: Set<OrderStatus> by lazy {
            setOf(
                New,
                Sending,
                UserSending,
                NonStopSending
            )
        }
    }
}
