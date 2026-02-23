package uz.yalla.data.util

fun Int?.or0(): Int = this ?: 0

fun Long?.or0(): Long = this ?: 0L

fun Double?.or0(): Double = this ?: 0.0

fun Float?.or0(): Float = this ?: 0f

fun Boolean?.orFalse(): Boolean = this ?: false
