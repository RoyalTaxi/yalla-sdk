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
 */
interface UserPreferences {

    /** User's first name, cached locally from the profile API. */
    val firstName: Flow<String>

    fun setFirstName(value: String)

    /** User's last name, cached locally from the profile API. */
    val lastName: Flow<String>
    fun setLastName(value: String)

    /** User's phone number (primary identifier). */
    val number: Flow<String>
    fun setNumber(value: String)

    /**
     * User's preferred payment method.
     *
     * Defaults to [PaymentKind.Cash] if no preference has been set.
     */
    val paymentType: Flow<PaymentKind>
    fun setPaymentType(value: PaymentKind)
}
