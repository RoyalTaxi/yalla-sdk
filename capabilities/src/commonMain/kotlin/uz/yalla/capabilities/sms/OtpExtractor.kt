package uz.yalla.capabilities.sms

fun extractOtp(
    message: String,
    length: Int,
    alphanumeric: Boolean
): String? {
    val pattern = if (alphanumeric) {
        Regex("(?<![A-Za-z0-9])(?=[A-Za-z0-9]*\\d)[A-Za-z0-9]{$length}(?![A-Za-z0-9])")
    } else {
        Regex("(?<!\\d)\\d{$length}(?!\\d)")
    }
    return pattern.find(message)?.value
}
