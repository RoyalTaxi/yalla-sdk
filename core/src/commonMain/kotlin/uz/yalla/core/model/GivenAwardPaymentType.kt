package uz.yalla.core.model

sealed class GivenAwardPaymentType(val typeName: String) {
    data object BALANCE : GivenAwardPaymentType("balance")

    data object PAYNET : GivenAwardPaymentType("paynet")

    companion object {
        fun fromTypeName(typeName: String): GivenAwardPaymentType =
            when (typeName) {
                BALANCE.typeName -> BALANCE
                PAYNET.typeName -> PAYNET
                else -> BALANCE
            }
    }
}
