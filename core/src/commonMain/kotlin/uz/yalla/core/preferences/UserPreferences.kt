package uz.yalla.core.preferences

import kotlinx.coroutines.flow.Flow
import uz.yalla.core.payment.PaymentMethod

interface UserPreferences {
    val firstName: Flow<String>

    fun setFirstName(value: String)

    val lastName: Flow<String>

    fun setLastName(value: String)

    val number: Flow<String>

    fun setNumber(value: String)

    val image: Flow<String>

    fun setImage(value: String)

    val gender: Flow<String>

    fun setGender(value: String)

    val birthday: Flow<String>

    fun setBirthday(value: String)

    val paymentMethod: Flow<PaymentMethod>

    fun setPaymentMethod(value: PaymentMethod)
}
