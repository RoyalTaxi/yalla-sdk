package uz.yalla.core.preferences

import kotlinx.coroutines.flow.Flow
import uz.yalla.core.payment.PaymentMethod

public interface UserPreferences {
    public val firstName: Flow<String>

    public fun setFirstName(value: String)

    public val lastName: Flow<String>

    public fun setLastName(value: String)

    public val number: Flow<String>

    public fun setNumber(value: String)

    public val image: Flow<String>

    public fun setImage(value: String)

    public val gender: Flow<String>

    public fun setGender(value: String)

    public val birthday: Flow<String>

    public fun setBirthday(value: String)

    public val paymentMethod: Flow<PaymentMethod>

    public fun setPaymentMethod(value: PaymentMethod)
}
