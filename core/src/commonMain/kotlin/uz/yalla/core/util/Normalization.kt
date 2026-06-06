package uz.yalla.core.util

internal fun String?.normalizedId(): String = this?.trim()?.lowercase().orEmpty()
