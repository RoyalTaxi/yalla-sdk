package uz.yalla.network

public data class NetworkConfig(
    val baseUrl: String,
    val brandId: String,
    val secretKey: String,
    val deviceType: String = "client",
    val deviceMode: String = "mobile",
    val guestAllowedSegments: List<String> = DEFAULT_GUEST_ALLOWED_SEGMENTS
)

public val DEFAULT_GUEST_ALLOWED_SEGMENTS: List<String> = listOf(
    "client",
    "valid",
    "register",
    "location-name",
    "cost",
    "lists"
)
