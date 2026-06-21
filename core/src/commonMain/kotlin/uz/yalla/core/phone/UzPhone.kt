package uz.yalla.core.phone

import uz.yalla.core.phone.UzPhone.Companion.COUNTRY_CODE
import uz.yalla.core.phone.UzPhone.Companion.NATIONAL_LENGTH
import uz.yalla.core.phone.UzPhone.Companion.of
import kotlin.jvm.JvmInline

/**
 * The single source of truth for Uzbek (+998) phone numbers across the SDK and its clients.
 *
 * Canonical internal form is the 9-digit NATIONAL number with no country code, no spaces,
 * no separators (e.g. "901234567") — this is what a client collects in its phone field.
 * Country code and display separators are derived, never stored, so there is exactly one
 * representation to reason about. Construction is closed: the only way to obtain a [UzPhone]
 * is through [of], which guarantees every instance holds a valid national number.
 */
@JvmInline
public value class UzPhone private constructor(
    /** The validated 9-digit national number, digits only (e.g. "901234567"). */
    public val national: String
) {
    /**
     * National display form `(90) 123 45 67`, matching the SDK phone field's visual transformation.
     */
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

    /** Canonical E.164 form with the leading `+` (e.g. "+998901234567"), for backend and dialing. */
    public fun toE164(): String = "+$COUNTRY_CODE$national"

    public companion object {
        /** Number of digits in a national UZ number, country code excluded. */
        public const val NATIONAL_LENGTH: Int = 9

        /** UZ country calling code, digits only, no `+`. */
        public const val COUNTRY_CODE: String = "998"

        /**
         * True when [national] is exactly [NATIONAL_LENGTH] ASCII digits. Pure; no normalization.
         * Use this to gate input that is already digits-only (e.g. a numeric phone field).
         */
        public fun isValidNational(national: String): Boolean =
            national.length == NATIONAL_LENGTH && national.all { it in '0'..'9' }

        /**
         * Parses any common UZ phone spelling into a [UzPhone], or `null` if it is not a valid
         * national number once normalized.
         *
         * Normalization strips every non-digit (`+`, spaces, parens, dashes) and then, if a country
         * code is present, the leading [COUNTRY_CODE] — so "+998 (90) 123 45 67", "998901234567",
         * and "901234567" all map to the same value. A bare "998901234567" is treated as 9 national
         * digits prefixed by the country code; a 9-digit input that itself starts with "998" is
         * impossible (that would be 12 digits), so there is no ambiguity.
         */
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
