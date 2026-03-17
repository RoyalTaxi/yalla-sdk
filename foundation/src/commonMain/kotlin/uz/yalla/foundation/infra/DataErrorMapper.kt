package uz.yalla.foundation.infra

import org.jetbrains.compose.resources.StringResource
import uz.yalla.core.error.DataError

/**
 * Maps [DataError] to user-friendly [StringResource] messages.
 *
 * Implement this interface to customize error message mapping per app or feature.
 *
 * @see DefaultDataErrorMapper for the default implementation
 * @since 0.0.1
 */
fun interface DataErrorMapper {
    /**
     * Maps a [DataError] to a localized user-facing message.
     *
     * @param error The data error to map
     * @return Localized message resource for the error
     * @since 0.0.1
     */
    fun map(error: DataError): StringResource
}
