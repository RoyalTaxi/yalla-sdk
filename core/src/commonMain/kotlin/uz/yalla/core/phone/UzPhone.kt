package uz.yalla.core.phone

import uz.yalla.core.phone.UzPhone.Companion.COUNTRY_CODE
import uz.yalla.core.phone.UzPhone.Companion.NATIONAL_LENGTH
import kotlin.jvm.JvmInline

@JvmInline
public value class UzPhone private constructor(
    public val national: String
) {
    public fun format(): String =
        buildString {
            national.forEachIndexed { index, digit ->
                when (index) {
                    0 -> append('(')
                    2 -> append(") ")
                    5, 7 -> append(' ')
                }
                append(digit)
            }
        }

    public fun toE164(): String = "+$COUNTRY_CODE$national"

    public companion object {
        public const val NATIONAL_LENGTH: Int = 9

        public const val COUNTRY_CODE: String = "998"

        public fun isValidNational(national: String): Boolean =
            national.length == NATIONAL_LENGTH && national.all { it in '0'..'9' }

        public fun of(input: String): UzPhone? {
            val digits = input.filter { it in '0'..'9' }
            val national = stripCountryCode(digits)
            return if (isValidNational(national)) UzPhone(national) else null
        }

        private fun stripCountryCode(digits: String): String =
            if (digits.length == COUNTRY_CODE.length + NATIONAL_LENGTH && digits.startsWith(COUNTRY_CODE)) {
                digits.substring(COUNTRY_CODE.length)
            } else {
                digits
            }
    }
}
