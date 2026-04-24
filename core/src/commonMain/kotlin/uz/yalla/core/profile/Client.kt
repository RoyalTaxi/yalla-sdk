package uz.yalla.core.profile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * User profile data returned from the API.
 *
 * Represents the complete client profile as received from the backend.
 * Locally-cached fields are stored in [uz.yalla.core.preferences.UserPreferences].
 *
 * @property phone Phone number (primary identifier, formatted with country code)
 * @property name First name
 * @property surname Last name
 * @property image Profile photo URL (may be empty if not set)
 * @property birthday Date of birth string in `yyyy-MM-dd` format (may be empty)
 * @property balance Account bonus balance in smallest currency unit
 * @property gender Gender identifier string, parseable by [GenderKind.from]
 * @see GenderKind
 * @see uz.yalla.core.preferences.UserPreferences
 * @since 0.0.1
 */
@Serializable
data class Client(
    @SerialName("phone") val phone: String,
    @SerialName("name") val name: String,
    @SerialName("surname") val surname: String,
    @SerialName("image") val image: String,
    @SerialName("birthday") val birthday: String,
    @SerialName("balance") val balance: Long,
    @SerialName("gender") val gender: String
)
