package uz.yalla.core.order

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

    data class Unknown(val originalId: String) : OrderStatus("unknown")

    companion object {
        fun from(id: String?): OrderStatus = when (id?.trim()?.lowercase()) {
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

        val active: Set<OrderStatus> = setOf(
            Appointed,
            AtAddress,
            InProgress
        )

        val ongoing: Set<OrderStatus> = setOf(
            New,
            Sending,
            UserSending,
            NonStopSending,
            Appointed,
            AtAddress,
            InProgress
        )

        val nonInteractive: Set<OrderStatus> = setOf(
            New,
            Sending,
            UserSending,
            NonStopSending
        )
    }
}
