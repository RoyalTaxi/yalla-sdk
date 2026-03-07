package uz.yalla.core.util

/**
 * Functional mapper converting [T] to [R].
 *
 * Used across data layers to map network DTOs to domain models.
 *
 * @since 0.0.4
 */
typealias Mapper<T, R> = (T) -> R
