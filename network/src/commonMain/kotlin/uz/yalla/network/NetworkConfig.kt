package uz.yalla.network

/**
 * Static configuration for the SDK's HTTP client: the values that shape every request but never change
 * during a client's life.
 *
 * @param baseUrl the backend origin all requests are resolved against.
 * @param brandId the `brand-id` header value.
 * @param secretKey the `secret-key` header value. **Not confidential:** it is a build-time constant
 *   baked into the distributed binary and is trivially extractable (CWE-798), so it must not be the
 *   sole authorization factor — treat it as a routing/brand tag, validated server-side, not a secret.
 * @param deviceType the `Device` header value.
 * @param deviceMode the `Device-Mode` header value.
 * @param guestAllowedPaths endpoints reachable before authentication; see [DEFAULT_GUEST_ALLOWED_PATHS].
 */
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
