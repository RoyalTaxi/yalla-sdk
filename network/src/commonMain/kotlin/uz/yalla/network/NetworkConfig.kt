package uz.yalla.network

public data class NetworkConfig(
    val baseUrl: String,
    val brandId: String,
    val secretKey: String,
    val deviceType: String = "client",
    val deviceMode: String = "mobile",
    val guestAllowedPaths: List<String> = DEFAULT_GUEST_ALLOWED_PATHS
)

/**
 * Full relative endpoint paths reachable before the user authenticates: send-code, validate, register,
 * reverse-geocoding, the tariff cost estimate and the executor list. See [isGuestAllowedPath] for how
 * these are matched against an outgoing request.
 */
public val DEFAULT_GUEST_ALLOWED_PATHS: List<String> =
    listOf(
        "client",
        "valid",
        "register",
        "location-name",
        "address/tariff/cost",
        "executor/lists"
    )
