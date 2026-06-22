package uz.yalla.core.profile

public data class Client(
    val phone: String,
    val name: String,
    val surname: String,
    val image: String,
    val birthday: String,
    val balance: Long,
    val gender: GenderKind
) {
    override fun toString(): String = "Client(gender=$gender, <redacted>)"
}
