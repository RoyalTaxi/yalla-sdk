package uz.yalla.core.util

/**
 * Returns this value or `0` if `null`.
 *
 * Convenience extension for safely unwrapping nullable API response fields.
 *
 * @return The receiver value, or `0`
 */
fun Int?.or0() = this ?: 0

/**
 * Returns this value or `0L` if `null`.
 *
 * Convenience extension for safely unwrapping nullable API response fields.
 *
 * @return The receiver value, or `0L`
 */
fun Long?.or0() = this ?: 0L

/**
 * Returns this value or `0f` if `null`.
 *
 * Convenience extension for safely unwrapping nullable API response fields.
 *
 * @return The receiver value, or `0f`
 */
fun Float?.or0() = this ?: 0f

/**
 * Returns this value or `0.0` if `null`.
 *
 * Convenience extension for safely unwrapping nullable API response fields.
 *
 * @return The receiver value, or `0.0`
 */
fun Double?.or0() = this ?: 0.0

/**
 * Returns this value or `false` if `null`.
 *
 * Convenience extension for safely unwrapping nullable API response fields.
 *
 * @return The receiver value, or `false`
 */
fun Boolean?.orFalse() = this ?: false

/**
 * Formats a [Long] as a money amount with non-breaking space thousand separators.
 *
 * Negative values are prefixed with a minus sign. This function does **not**
 * append a currency symbol; callers should add one as needed.
 *
 * ## Usage
 * ```kotlin
 * 400000L.formatMoney()   // "400 000"
 * 1234567L.formatMoney()  // "1 234 567"
 * (-5000L).formatMoney()  // "-5 000"
 * 0L.formatMoney()        // "0"
 * ```
 *
 * @return Formatted money string with space-separated thousands
 */
fun Long.formatMoney(): String {
    val isNegative = this < 0
    val absValue = if (isNegative) -this else this
    val str = absValue.toString()
    val result =
        buildString {
            str.reversed().forEachIndexed { index, char ->
                if (index > 0 && index % 3 == 0) append(' ')
                append(char)
            }
        }.reversed()
    return if (isNegative) "-$result" else result
}

/**
 * Formats a nullable [Long] as a money amount, treating `null` as `0`.
 *
 * @return Formatted money string, or `"0"` if the receiver is `null`
 * @see formatMoney
 */
fun Long?.formatMoney(): String = (this ?: 0L).formatMoney()
