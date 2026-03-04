package uz.yalla.core.profile

/**
 * User profile data returned from the API.
 *
 * @property phone Phone number (primary identifier)
 * @property name First name
 * @property surname Last name
 * @property image Profile photo URL
 * @property birthday Date of birth string
 * @property balance Account balance in smallest currency unit
 * @property gender Gender identifier string
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
