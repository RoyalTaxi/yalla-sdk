package uz.yalla.primitives.pin

/**
 * State for [LocationPin] component.
 *
 * @property address Address label displayed above the pin.
 * @property timeout Countdown time in minutes (null for no countdown).
 * @property jumping Whether the pin is animating (searching state).
 * @property loading Whether the pin shows loading indicator.
 * @since 0.0.1
 */
data class LocationPinState(
    val address: String? = null,
    val timeout: Int? = null,
    val jumping: Boolean = false,
    val loading: Boolean = false
)
