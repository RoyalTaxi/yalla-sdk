package uz.yalla.network

public data class CertificatePin(
    val host: String,
    val pins: List<String>
)

internal fun pinPatternMatchesHost(
    pattern: String,
    hostname: String
): Boolean {
    val canonicalPattern = pattern.lowercase()
    val canonicalHost = hostname.lowercase()
    return when {
        canonicalPattern.startsWith("**.") -> {
            val suffix = canonicalPattern.substring(3)
            canonicalHost == suffix || canonicalHost.endsWith(".$suffix")
        }
        canonicalPattern.startsWith("*.") -> {
            val suffix = canonicalPattern.substring(2)
            val prefixLength = canonicalHost.length - suffix.length - 1
            canonicalHost.endsWith(".$suffix") &&
                prefixLength > 0 &&
                canonicalHost.lastIndexOf('.', prefixLength - 1) == -1
        }

        else -> canonicalHost == canonicalPattern
    }
}
