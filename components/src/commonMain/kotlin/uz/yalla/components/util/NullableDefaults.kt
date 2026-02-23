package uz.yalla.components.util

/**
 * Returns this value or 0 if null.
 */
fun Int?.or0() = this ?: 0

/**
 * Returns this value or 0 if null.
 */
fun Long?.or0() = this ?: 0L

/**
 * Returns this value or 0f if null.
 */
fun Float?.or0() = this ?: 0f

/**
 * Returns this value or 0.0 if null.
 */
fun Double?.or0() = this ?: 0.0

/**
 * Returns this value or false if null.
 */
fun Boolean?.orFalse() = this ?: false

/**
 * Returns this value or true if null.
 */
fun Boolean?.orTrue() = this ?: true

/**
 * Formats a Long as a money amount with space thousand separators.
 *
 * ## Example
 * ```kotlin
 * 400000L.formatMoney()   // "400 000"
 * 1234567L.formatMoney()  // "1 234 567"
 * ```
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
 * Formats a nullable Long as a money amount, returns "0" if null.
 */
fun Long?.formatMoney(): String = (this ?: 0L).formatMoney()
