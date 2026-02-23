package uz.yalla.core.status

sealed class OrderStatus(
    val id: String
) {
    data object New : OrderStatus("new")

    data object Sending : OrderStatus("sending")

    data object UserSending : OrderStatus("user_sending")

    data object NonStopSending : OrderStatus("nonstop_sending")

    data object Appointed : OrderStatus("appointed")

    data object AtAddress : OrderStatus("at_address")

    data object InProgress : OrderStatus("in_progress")

    data object Completed : OrderStatus("completed")

    data object Canceled : OrderStatus("canceled")

    data class Unknown(
        val originalId: String,
    ) : OrderStatus("unknown")

    companion object {
        fun from(id: String?): OrderStatus =
            when (id?.lowercase()) {
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

        val entries: List<OrderStatus> =
            listOf(
                New,
                Sending,
                UserSending,
                NonStopSending,
                Appointed,
                AtAddress,
                InProgress,
                Completed,
                Canceled,
                Unknown("unknown")
            )

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

        val finished: Set<OrderStatus> = setOf(Completed, Canceled)

        val cancellable: Set<OrderStatus> =
            setOf(
                New,
                Sending,
                UserSending,
                NonStopSending,
                Appointed,
                AtAddress
            )

        /** Statuses where order is being processed/sent but no driver assigned yet */
        val nonInteractive: Set<OrderStatus> =
            setOf(
                New,
                Sending,
                UserSending,
                NonStopSending
            )

        /** Statuses where ride is in progress (driver at address or driving) */
        val rideInProgress: Set<OrderStatus> =
            setOf(
                AtAddress,
                InProgress
            )
    }

    val isActive: Boolean get() = this in active
    val isOngoing: Boolean get() = this in ongoing
    val isFinished: Boolean get() = this in finished
    val isCancellable: Boolean get() = this in cancellable
    val isNonInteractive: Boolean get() = this in nonInteractive
    val isRideInProgress: Boolean get() = this in rideInProgress
}
