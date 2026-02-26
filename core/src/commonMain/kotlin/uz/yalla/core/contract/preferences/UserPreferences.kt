package uz.yalla.core.contract.preferences

import kotlinx.coroutines.flow.Flow
import uz.yalla.core.kind.PaymentKind

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
