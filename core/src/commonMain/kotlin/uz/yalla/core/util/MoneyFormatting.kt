package uz.yalla.core.util

public fun Long.formatMoney(): String {
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

public fun Long?.formatMoney(): String = (this ?: 0L).formatMoney()
