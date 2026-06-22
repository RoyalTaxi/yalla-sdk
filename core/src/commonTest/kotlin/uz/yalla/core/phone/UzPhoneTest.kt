package uz.yalla.core.phone

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UzPhoneTest {
    private val national = "901234567"

    @Test
    fun constantsMatchTheUzContract() {
        assertEquals(9, UzPhone.NATIONAL_LENGTH)
        assertEquals("998", UzPhone.COUNTRY_CODE)
    }

    @Test
    fun isValidNationalAcceptsExactlyNineDigits() {
        assertTrue(UzPhone.isValidNational("901234567"))
        assertTrue(UzPhone.isValidNational("000000000"))
    }

    @Test
    fun isValidNationalRejectsWrongLength() {
        assertFalse(UzPhone.isValidNational(""))
        assertFalse(UzPhone.isValidNational("90123456"))
        assertFalse(UzPhone.isValidNational("9012345678"))
    }

    @Test
    fun isValidNationalRejectsNonDigits() {
        assertFalse(UzPhone.isValidNational("90123456a"))
        assertFalse(UzPhone.isValidNational("(90)12345"))
        assertFalse(UzPhone.isValidNational("+99890123"))
    }

    @Test
    fun ofAcceptsBareNationalNumber() {
        assertEquals(national, UzPhone.of("901234567")?.national)
    }

    @Test
    fun ofStripsCountryCodeFromE164Input() {
        assertEquals(national, UzPhone.of("998901234567")?.national)
        assertEquals(national, UzPhone.of("+998901234567")?.national)
        assertEquals(national, UzPhone.of("+998 (90) 123 45 67")?.national)
    }

    @Test
    fun ofRejectsMalformedInput() {
        assertNull(UzPhone.of(""))
        assertNull(UzPhone.of("90123456"))
        assertNull(UzPhone.of("12345678901234"))
        assertNull(UzPhone.of("abc"))
    }

    @Test
    fun formatRendersNationalDisplaySpacing() {
        assertEquals("(90) 123 45 67", UzPhone.of(national)!!.format())
    }

    @Test
    fun toE164PrependsPlusAndCountryCode() {
        assertEquals("+998901234567", UzPhone.of(national)!!.toE164())
    }

    @Test
    fun parseThenE164IsStableAcrossInputSpellings() {
        val fromNational = UzPhone.of("901234567")!!
        val fromE164 = UzPhone.of("+998 90 123 45 67")!!
        assertEquals(fromNational.toE164(), fromE164.toE164())
        assertEquals(fromNational, fromE164)
    }
}
