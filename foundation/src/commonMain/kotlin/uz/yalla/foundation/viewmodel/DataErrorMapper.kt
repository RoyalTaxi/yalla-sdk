package uz.yalla.foundation.viewmodel

import org.jetbrains.compose.resources.StringResource
import uz.yalla.core.error.DataError

/**
 * Maps [DataError] to user-friendly [StringResource] messages.
 *
 * Implement this interface to customize error message mapping per app or feature.
 *
 * @see DefaultDataErrorMapper for the default implementation
 */
fun interface DataErrorMapper {
    fun map(error: DataError): StringResource
}
