package uz.yalla.core.util

fun Int?.or0() = this ?: 0

fun Long?.or0() = this ?: 0L

fun Float?.or0() = this ?: 0f

fun Double?.or0() = this ?: 0.0

fun Boolean?.orFalse() = this ?: false
