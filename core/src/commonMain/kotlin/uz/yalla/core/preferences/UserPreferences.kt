package uz.yalla.core.preferences

import kotlinx.coroutines.flow.Flow
import uz.yalla.core.payment.PaymentKind

/**
 * Contract for user profile data storage.
 *
 * Persists locally-cached user profile fields and the preferred payment method.
 * All properties are reactive [Flow]s that emit whenever the underlying value changes.
 *
 * Cleared by [SessionPreferences.clearSession] on logout.
 *
 * @see uz.yalla.core.profile.Client for the full API profile model
 * @see SessionPreferences.clearSession
 * @since 0.0.1
 */
interface UserPreferences {

    /** User's first name, cached locally from the profile API. */
    val firstName: Flow<String>

    /**
     * Persists the user's first name.
     *
     * @param value First name string
     */
    fun setFirstName(value: String)

    /** User's last name, cached locally from the profile API. */
    val lastName: Flow<String>

    /**
     * Persists the user's last name.
     *
     * @param value Last name string
     */
    fun setLastName(value: String)

    /** User's phone number (primary identifier). */
    val number: Flow<String>

    /**
     * Persists the user's phone number.
     *
     * @param value Phone number string
     */
    fun setNumber(value: String)

    /**
     * User's preferred payment method.
     *
     * Defaults to [PaymentKind.Cash] if no preference has been set.
     */
    val paymentType: Flow<PaymentKind>

    /**
     * Persists the user's preferred payment method.
     *
     * @param value The selected [PaymentKind]
     * @see PaymentKind
     */
    fun setPaymentType(value: PaymentKind)
}
