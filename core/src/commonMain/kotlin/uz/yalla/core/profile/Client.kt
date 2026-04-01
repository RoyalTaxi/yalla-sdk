package uz.yalla.core.profile

/**
 * User profile data returned from the API.
 *
 * Represents the complete client profile as received from the backend.
 * Locally-cached fields are stored in [uz.yalla.core.contract.preferences.UserPreferences].
 *
 * @property phone Phone number (primary identifier, formatted with country code)
 * @property name First name
 * @property surname Last name
 * @property image Profile photo URL (may be empty if not set)
 * @property birthday Date of birth string in `yyyy-MM-dd` format (may be empty)
 * @property balance Account bonus balance in smallest currency unit
 * @property gender Gender identifier string, parseable by [GenderKind.from]
 * @see GenderKind
 * @see uz.yalla.core.contract.preferences.UserPreferences
 * @since 0.0.1
 */
data class Client(
    val phone: String,
    val name: String,
    val surname: String,
    val image: String,
    val birthday: String,
    val balance: Long,
    val gender: String
)
