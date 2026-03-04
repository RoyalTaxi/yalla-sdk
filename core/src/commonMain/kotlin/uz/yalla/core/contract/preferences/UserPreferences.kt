package uz.yalla.core.contract.preferences

import kotlinx.coroutines.flow.Flow
import uz.yalla.core.payment.PaymentKind

/**
 * Contract for user profile data storage.
 *
 * Persists locally-cached user profile fields and preferred payment method.
 * All properties are reactive [Flow]s.
 *
 * @since 0.0.1
 */
interface UserPreferences {
    val firstName: Flow<String>

    fun setFirstName(value: String)

    val lastName: Flow<String>

    fun setLastName(value: String)

    val number: Flow<String>

    fun setNumber(value: String)

    val paymentType: Flow<PaymentKind>

    fun setPaymentType(value: PaymentKind)
}
