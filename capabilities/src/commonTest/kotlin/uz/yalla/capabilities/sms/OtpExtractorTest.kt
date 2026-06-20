package uz.yalla.capabilities.sms

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Output-based characterization of [extractOtp].
 *
 * Pins the anchor-then-fallback precedence: a code following a `code`/`kod`/`код`
 * keyword wins; otherwise the first standalone match wins (including the documented
 * limitation that a keyword with no code after it falls back to the first global
 * match — see [keywordPresentButNoFollowingCodeFallsBackToFirstMatch]). Also pins the
 * no-slicing guarantee for phone numbers / long digit-runs and the degenerate-length
 * boundary (length out of `1..12` returns `null`, never throws).
 */
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
    fun keywordPresentButNoFollowingCodeFallsBackToFirstMatch() {
        // Documented limitation (M8): when the keyword has no code after it the search
        // falls back to the first global match, which preserves the legitimate
        // code-before-keyword case (see extractsFiveDigitCodeAtMessageStart) at the cost
        // of returning a preceding decoy when there is no real code. Pinned intentionally
        // so a future change to this trade-off is a conscious, reviewed decision.
        assertEquals("482913", extractOtp("482913 is your kod", 6, true))
        assertEquals("777777", extractOtp("777777 sent. Your code expired", 6, false))
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

    @Test
    fun numericSurfaceAnchorBeatsEarlierDecoyRun() {
        // The default Uzbek OTP shape: a decoy run precedes the keyword, real code follows.
        assertEquals("12345", extractOtp("Order 99999. Your code is 12345", 5, false))
    }

    @Test
    fun allLetterTokenOfRequestedLengthIsRejectedOnAlphanumericSurface() {
        // Pins the digit-required lookahead: a right-length all-letter token must fail.
        assertNull(extractOtp("Your code is ABCDEF", 6, true))
    }

    @Test
    fun zeroLengthReturnsNullInsteadOfEmptyCode() {
        assertNull(extractOtp("Your code is 12345", 0, false))
    }

    @Test
    fun negativeLengthReturnsNullWithoutThrowing() {
        assertNull(extractOtp("Your code is 12345", -1, false))
    }

    @Test
    fun lengthAboveMaxReturnsNull() {
        assertNull(extractOtp("Your code is 1234567890123", 13, false))
    }
}
