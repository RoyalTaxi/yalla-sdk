package uz.yalla.core.profile

/**
 * The signed-in user's profile. Carries PII (phone, name, birthday, balance), so its [toString]
 * is redacted to prevent the auto-generated data-class string from leaking those fields into a log
 * line or crash report (CWE-532). Equality/`copy`/destructuring keep the full data-class behavior.
 */
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
