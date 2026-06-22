package uz.yalla.network

public data class NetworkConfig(
    val baseUrl: String,
    val brandId: String,
    val secretKey: String,
    val deviceType: String = "client",
    val deviceMode: String = "mobile",
    val guestAllowedPaths: List<String> = DEFAULT_GUEST_ALLOWED_PATHS,
    val certificatePins: List<CertificatePin> = emptyList()
)

public val DEFAULT_GUEST_ALLOWED_PATHS: List<String> =
    listOf(
        "client",
        "valid",
        "register",
        "location-name",
        "address/tariff/cost",
        "executor/lists"
    )
