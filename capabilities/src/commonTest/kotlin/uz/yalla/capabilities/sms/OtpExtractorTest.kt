package uz.yalla.capabilities.sms

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class OtpExtractorTest {
    @Test
    fun extractsPlainFiveDigitCode() {
        assertEquals("12345", extractOtp("12345", 5, false))
    }

    @Test
    fun extractsFiveDigitCodeMidSentence() {
        assertEquals("48613", extractOtp("Your Yalla code is 48613, do not share it", 5, false))
    }

    @Test
    fun extractsFiveDigitCodeAtMessageStart() {
        assertEquals("48613", extractOtp("48613 is your verification code", 5, false))
    }

    @Test
    fun extractsFiveDigitCodeAtMessageEnd() {
        assertEquals("48613", extractOtp("Your verification code: 48613", 5, false))
    }

    @Test
    fun doesNotSlicePhoneNumbers() {
        assertNull(extractOtp("Call +998901234567 for support", 5, false))
    }

    @Test
    fun doesNotSliceLongDigitRuns() {
        assertNull(extractOtp("Transaction 123456789012 received", 5, false))
    }

    @Test
    fun extractsAlphanumericCodeWithDigit() {
        assertEquals("A1B2C3", extractOtp("Your card code is A1B2C3", 6, true))
    }

    @Test
    fun ignoresDigitFreeWordsOnAlphanumericSurface() {
        assertNull(extractOtp("Sample wallet topup failed", 6, true))
    }

    @Test
    fun documentOrderWinsWhenCodePrecedesAmount() {
        assertEquals("A1B2C3", extractOtp("Code: A1B2C3. Amount: 100000", 6, true))
    }

    @Test
    fun codeKeywordAnchorsExtractionWhenAmountPrecedesCode() {
        assertEquals("A1B2C3", extractOtp("Amount: 100000. Code: A1B2C3", 6, true))
    }

    @Test
    fun amountBeforeKeywordNeverWinsOnDigitRunSurface() {
        assertEquals("482913", extractOtp("Summa 100000. Kod: 482913", 6, true))
    }

    @Test
    fun keywordAfterCodeFallsBackToFirstMatch() {
        assertEquals("482913", extractOtp("482913 is your kod", 6, true))
    }

    @Test
    fun allDigitCodeMatchesOnAlphanumericSurface() {
        assertEquals("482913", extractOtp("Kod: 482913", 6, true))
    }

    @Test
    fun extractsCodeFromCyrillicText() {
        val message = "Ваш код подтверждения 48613. Никому не сообщайте его"
        assertEquals("48613", extractOtp(message, 5, false))
    }

    @Test
    fun returnsNullWhenNothingMatches() {
        assertNull(extractOtp("Welcome to Yalla", 5, false))
    }

    @Test
    fun extractedLengthAlwaysEqualsRequestedLength() {
        assertEquals(6, extractOtp("Use code 123456 now", 6, false)?.length)
        assertNull(extractOtp("Use code 12345 now", 6, false))
    }
}
