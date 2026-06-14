package uz.yalla.capabilities.sms

private val CodeKeyword = Regex("(?:kod|code|код)", RegexOption.IGNORE_CASE)

public fun extractOtp(
    message: String,
    length: Int,
    alphanumeric: Boolean
): String? {
    val pattern =
        if (alphanumeric) {
            Regex("(?<![A-Za-z0-9])(?=[A-Za-z0-9]*\\d)[A-Za-z0-9]{$length}(?![A-Za-z0-9])")
        } else {
            Regex("(?<!\\d)\\d{$length}(?!\\d)")
        }
    val anchor = CodeKeyword.find(message)
    return anchor?.let { pattern.find(message, it.range.last + 1)?.value } ?: pattern.find(message)?.value
}
