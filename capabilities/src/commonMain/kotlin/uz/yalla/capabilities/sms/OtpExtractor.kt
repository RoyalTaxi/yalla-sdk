package uz.yalla.capabilities.sms

private val CodeKeyword = Regex("(?:kod|code|код)", RegexOption.IGNORE_CASE)

private const val MAX_CODE_LENGTH = 12

/**
 * Extracts an OTP of exactly [length] characters from an SMS [message], or `null`
 * if none is found.
 *
 * Prefers a code that follows a `code`/`kod`/`код` keyword (the anchor); if no code
 * follows the keyword (or no keyword is present) it falls back to the first
 * standalone match anywhere in the text. Known limitation: when a keyword is present
 * but the code precedes it (or no real code exists), the first global match wins,
 * so a decoy digit-run before the keyword can be returned — see
 * `OtpExtractorTest.keywordPresentButNoFollowingCodeFallsBackToFirstMatch`.
 *
 * When [alphanumeric] is `true` the code may contain letters but must include at
 * least one digit; otherwise it must be all digits. Digit-runs longer than
 * [length] (phone numbers, transaction ids) are never sliced.
 *
 * [length] must be in `1..12`; any other value returns `null` (the function never
 * throws on a misconfigured length).
 */
internal fun extractOtp(
    message: String,
    length: Int,
    alphanumeric: Boolean
): String? {
    if (length !in 1..MAX_CODE_LENGTH) return null
    val pattern =
        if (alphanumeric) {
            Regex("(?<![A-Za-z0-9])(?=[A-Za-z0-9]*\\d)[A-Za-z0-9]{$length}(?![A-Za-z0-9])")
        } else {
            Regex("(?<!\\d)\\d{$length}(?!\\d)")
        }
    val anchor = CodeKeyword.find(message)
    return anchor?.let { pattern.find(message, it.range.last + 1)?.value } ?: pattern.find(message)?.value
}
