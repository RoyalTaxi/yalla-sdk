package uz.yalla.core.model

data class Client(
    val phone: String,
    val name: String,
    val surname: String,
    val image: String,
    val birthday: String,
    val balance: Long,
    val gender: String
)
