package uz.yalla.core.util

public fun Int?.or0(): Int = this ?: 0

public fun Long?.or0(): Long = this ?: 0L

public fun Float?.or0(): Float = this ?: 0f

public fun Double?.or0(): Double = this ?: 0.0

public fun Boolean?.orFalse(): Boolean = this ?: false
